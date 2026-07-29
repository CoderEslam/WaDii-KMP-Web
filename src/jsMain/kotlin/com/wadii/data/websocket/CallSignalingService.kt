package com.wadii.data.websocket

import com.wadii.core.fromJson
import com.wadii.core.toJson
import com.wadii.data.api.json
import com.wadii.domain.model.call.CallSignal
import com.wadii.domain.model.call.CallSocketFrame
import com.wadii.domain.model.call.IncomingCallSignal
import com.wadii.domain.model.chat.SocketEvent
import com.wadii.domain.model.chat.SocketResponse
import com.wadii.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class CallSignalingService(private val client: HttpClient) {

    private var sendText: (suspend (String) -> Unit)? = null

    private val callEventNames = setOf(
        SocketEvent.CALL_INVITE.name,
        SocketEvent.CALL_ACCEPT.name,
        SocketEvent.CALL_REJECT.name,
        SocketEvent.CALL_END.name
    )

    fun connect(userId: Long, token: String): Flow<IncomingCallSignal> = callbackFlow {
        val job = launch {
            runCatching {
                client.webSocket("${Constants.WS_URL}/$userId?token=$token") {
                    sendText = { text -> send(Frame.Text(text)) }
                    try {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val socketResponse = frame.readText().fromJson<SocketResponse>()
                                val eventName = socketResponse?.event
                                if (eventName != null && eventName in callEventNames) {
                                    val signal = socketResponse.data.toJson().fromJson<CallSignal>()
                                    if (signal != null) {
                                        trySend(IncomingCallSignal(SocketEvent.valueOf(eventName), signal))
                                    }
                                }
                            }
                        }
                    } finally {
                        sendText = null
                    }
                }
            }.onFailure { sendText = null }
        }
        awaitClose {
            sendText = null
            job.cancel()
        }
    }

    suspend fun send(event: SocketEvent, signal: CallSignal): Boolean {
        val action = sendText ?: return false
        runCatching {
            action.invoke(json().encodeToString(CallSocketFrame.serializer(), CallSocketFrame(event.name, signal)))
        }
        return true
    }
}
