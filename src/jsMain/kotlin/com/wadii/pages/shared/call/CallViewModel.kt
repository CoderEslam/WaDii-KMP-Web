package com.wadii.pages.shared.call

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.core.call.CallSignalingController
import com.wadii.data.livekit.LiveKitCallClient
import com.wadii.data.livekit.LiveKitClient
import com.wadii.domain.model.call.CallSignal
import com.wadii.domain.model.call.livekit.LiveKitTokenRequest
import com.wadii.domain.model.chat.SocketEvent
import com.wadii.domain.usecase.LiveKitUseCase
import com.wadii.state.AppState
import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.HTMLMediaElement

class CallViewModel(
    private val channelName: String,
    private val remoteUserId: Long,
    private val remoteUserName: String,
    private val remoteUserImage: String?,
    private val withVideo: Boolean,
    private val isCaller: Boolean,
    private val liveKitUseCase: LiveKitUseCase,
    private val callSignaling: CallSignalingController
) : BaseViewModel<CallState, CallEvent>() {

    private val liveKitClient = LiveKitCallClient()
    private val remoteAudioElements = mutableListOf<HTMLMediaElement>()
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
                        cleanupLiveKit()
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
        liveKitUseCase.getToken(
            LiveKitTokenRequest(
                roomName = channelName,
                identity = uid.toString(),
                participantName = AppState.user?.fullName ?: ""
            )
        ) { r ->
            r.handelState(
                onSuccess = { resp ->
                    screenModelScope.launch(Dispatchers.Default) {
                        runCatching {
                            withContext(Dispatchers.Main) {
                                liveKitClient.onTrackSubscribed = { track, participant ->
                                    onRemoteTrackSubscribed(track, participant)
                                }
                                liveKitClient.onParticipantDisconnected = { onRemoteLeft() }
                                val localTrack = liveKitClient.join(resp.data.url, resp.data.token, withVideo)
                                updateState {
                                    it.copy(
                                        status = CallStatus.CONNECTED,
                                        localVideoTrack = localTrack
                                    )
                                }
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

    private fun onRemoteTrackSubscribed(
        track: LiveKitClient.Track,
        participant: LiveKitClient.RemoteParticipant
    ) {
        when (track.kind) {
            "video" -> updateState {
                it.copy(remoteVideoTrack = track, remoteHasVideo = true)
            }

            "audio" -> {
                val element = track.attach()
                document.body?.appendChild(element)
                remoteAudioElements += element
            }
        }
    }

    private fun onRemoteLeft() {
        screenModelScope.launch { cleanupLiveKit() }
        updateState { it.copy(status = CallStatus.ENDED) }
    }

    private fun toggleMic() = screenModelScope.launch {
        val enabled = !state.value.micEnabled
        liveKitClient.setMicEnabled(enabled)
        updateState { it.copy(micEnabled = enabled) }
    }

    private fun toggleCamera() = screenModelScope.launch {
        val enabled = !state.value.cameraEnabled
        liveKitClient.setCameraEnabled(enabled)
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
        cleanupLiveKit()
        updateState { it.copy(status = CallStatus.ENDED) }
    }

    private suspend fun cleanupLiveKit() {
        runCatching { liveKitClient.leave() }
        remoteAudioElements.forEach { it.parentNode?.removeChild(it) }
        remoteAudioElements.clear()
    }

    override fun onDispose() {
        signalJob?.cancel()
        screenModelScope.launch { cleanupLiveKit() }
    }
}
