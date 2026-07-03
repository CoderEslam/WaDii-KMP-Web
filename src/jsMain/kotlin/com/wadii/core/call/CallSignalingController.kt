package com.wadii.core.call

import com.wadii.data.websocket.CallSignalingService
import com.wadii.domain.model.call.CallSignal
import com.wadii.domain.model.call.IncomingCallSignal
import com.wadii.domain.model.chat.SocketEvent
import com.wadii.state.AppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class CallSignalingController(private val service: CallSignalingService) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    val events = MutableSharedFlow<IncomingCallSignal>(extraBufferCapacity = 8)

    fun start(userId: Int, token: String) {
        if (job?.isActive == true) return
        job = scope.launch {
            service.connect(userId, token).collect { incoming ->
                when (incoming.event) {
                    SocketEvent.CALL_INVITE -> AppState.incomingCall = incoming.signal
                    SocketEvent.CALL_END, SocketEvent.CALL_REJECT ->
                        if (AppState.incomingCall?.channelName == incoming.signal.channelName) {
                            AppState.incomingCall = null
                        }
                    else -> {}
                }
                events.emit(incoming)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    suspend fun send(event: SocketEvent, signal: CallSignal): Boolean = service.send(event, signal)
}
