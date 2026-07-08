package com.wadii.pages.shared.call

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.core.call.CallSignalingController
import com.wadii.data.agora.AgoraCallClient
import com.wadii.data.agora.IAgoraRTCRemoteUser
import com.wadii.data.agora.IRemoteAudioTrack
import com.wadii.data.agora.IRemoteVideoTrack
import com.wadii.domain.model.call.CallSignal
import com.wadii.domain.model.call.agora.AgoraTokenRequest
import com.wadii.domain.model.chat.SocketEvent
import com.wadii.domain.usecase.AgoraUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CallViewModel(
    private val channelName: String,
    private val remoteUserId: Long,
    private val remoteUserName: String,
    private val remoteUserImage: String?,
    private val withVideo: Boolean,
    private val isCaller: Boolean,
    private val agoraUseCase: AgoraUseCase,
    private val callSignaling: CallSignalingController
) : BaseViewModel<CallState, CallEvent>() {

    private val agoraClient = AgoraCallClient()
    private var signalJob: Job? = null

    override val initialState: CallState
        get() = CallState(
            status = if (isCaller) CallStatus.CALLING else CallStatus.CONNECTING,
            isCaller = isCaller,
            withVideo = withVideo,
            cameraEnabled = withVideo,
            remoteUserName = remoteUserName,
            remoteUserImage = remoteUserImage
        )

    override val state: StateFlow<CallState> =
        _state.stateIn(screenModelScope, SharingStarted.Eagerly, initialState)

    init {
        observeSignaling()
        if (isCaller) sendInvite() else joinAndPublish()
    }

    override fun onEvent(event: CallEvent) {
        when (event) {
            CallEvent.ToggleMic -> toggleMic()
            CallEvent.ToggleCamera -> toggleCamera()
            CallEvent.EndCall -> endCall()
        }
    }

    private fun observeSignaling() {
        signalJob = screenModelScope.launch {
            callSignaling.events.collect { incoming ->
                if (incoming.signal.channelName != channelName) return@collect
                when (incoming.event) {
                    SocketEvent.CALL_ACCEPT -> if (isCaller) joinAndPublish()
                    SocketEvent.CALL_REJECT ->
                        updateState { it.copy(status = CallStatus.ENDED, error = "Call declined") }

                    SocketEvent.CALL_END -> {
                        cleanupAgora()
                        updateState { it.copy(status = CallStatus.ENDED) }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun sendInvite() = screenModelScope.launch {
        callSignaling.send(
            SocketEvent.CALL_INVITE,
            CallSignal(
                channelName = channelName,
                callType = if (withVideo) "VIDEO" else "AUDIO",
                fromUserId = AppState.user?.id ?: 0L,
                toUserId = remoteUserId,
                fromUserName = AppState.user?.fullName ?: "",
                fromUserImage = AppState.user?.image
            )
        )
    }

    private fun joinAndPublish() = screenModelScope.launch {
        updateState { it.copy(status = CallStatus.CONNECTING) }
        val uid = AppState.user?.id ?: return@launch
        agoraUseCase.getToken(AgoraTokenRequest(channelName, uid)) { r ->
            r.handelState(
                onSuccess = { resp ->
                    screenModelScope.launch(Dispatchers.Default) {
                        runCatching {
                            try {
                                withContext(Dispatchers.Main) {
                                    agoraClient.onUserPublished =
                                        { user, mediaType -> onRemotePublished(user, mediaType) }
                                    agoraClient.onUserLeft = { onRemoteLeft() }
                                    val localCam = agoraClient.join(
                                        resp.data.appId,
                                        channelName,
                                        resp.data.token,
                                        uid,
                                        withVideo
                                    )
                                    localCam?.let { localCam ->
                                        updateState {
                                            it.copy(
                                                status = CallStatus.CONNECTED,
                                                localVideoTrack = localCam
                                            )
                                        }
                                    }
                                }
                            } catch (e: dynamic) {
                                console.log(e)
                                console.log(e.code)
                                console.log(e.message)
                            }
                        }.onFailure { e ->
                            updateState {
                                it.copy(
                                    status = CallStatus.ENDED,
                                    error = e.message ?: "Failed to join call"
                                )
                            }

                        }
                    }
                },
                onError = { msg, _ ->
                    updateState {
                        it.copy(
                            status = CallStatus.ENDED,
                            error = msg
                        )
                    }
                }
            )
        }
    }

    private fun onRemotePublished(user: IAgoraRTCRemoteUser, mediaType: String) =
        screenModelScope.launch {
            val track = agoraClient.subscribe(user, mediaType) ?: return@launch
            when (mediaType) {
                "video" -> updateState {
                    it.copy(
                        remoteVideoTrack = track.unsafeCast<IRemoteVideoTrack>(),
                        remoteHasVideo = true
                    )
                }

                "audio" -> track.unsafeCast<IRemoteAudioTrack>().play()
            }
        }

    private fun onRemoteLeft() {
        screenModelScope.launch { cleanupAgora() }
        updateState { it.copy(status = CallStatus.ENDED) }
    }

    private fun toggleMic() = screenModelScope.launch {
        val enabled = !state.value.micEnabled
        agoraClient.setMicEnabled(enabled)
        updateState { it.copy(micEnabled = enabled) }
    }

    private fun toggleCamera() = screenModelScope.launch {
        val enabled = !state.value.cameraEnabled
        agoraClient.setCameraEnabled(enabled)
        updateState { it.copy(cameraEnabled = enabled) }
    }

    private fun endCall(notifyPeer: Boolean = true) = screenModelScope.launch {
        if (notifyPeer) {
            callSignaling.send(
                SocketEvent.CALL_END,
                CallSignal(
                    channelName = channelName,
                    fromUserId = AppState.user?.id ?: 0,
                    toUserId = remoteUserId
                )
            )
        }
        cleanupAgora()
        updateState { it.copy(status = CallStatus.ENDED) }
    }

    private suspend fun cleanupAgora() {
        runCatching { agoraClient.leave() }
    }

    override fun onDispose() {
        signalJob?.cancel()
        screenModelScope.launch { cleanupAgora() }
    }
}
