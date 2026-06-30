package com.wadii.data.websocket

import com.wadii.core.fromJson
import com.wadii.core.toJson
import com.wadii.data.api.json
import com.wadii.domain.model.chat.InsertMessage
import com.wadii.domain.model.chat.PageMessages
import com.wadii.domain.model.chat.Presence
import com.wadii.domain.model.chat.SocketEvent
import com.wadii.domain.model.chat.SocketEvent.*
import com.wadii.domain.model.chat.SocketResponse
import com.wadii.state.AppState
import com.wadii.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class ChatWebSocketService(private val client: HttpClient) {

    private var sendText: (suspend (String) -> Unit)? = null

    fun connect(userId: Int, token: String): Flow<PageMessages.Content> = callbackFlow {
        val job = launch {
            runCatching {
                client.webSocket("${Constants.WS_URL}/$userId?token=$token") {
                    println("Connected to WS")
                    sendText = { text -> send(Frame.Text(text)) }
                    try {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val socketResponse = frame.readText().fromJson<SocketResponse>()
//                                AppState.toast(socketResponse.toString())
                                when (socketResponse?.event) {
                                    PRESENCE.name -> {
                                        val presence =
                                            socketResponse.data.toJson().fromJson<Presence>()
//                                        AppState.toast(presence.toJson())
                                    }

                                    MESSAGE.name -> {
                                        val content =
                                            socketResponse.data.toJson()
                                                .fromJson<PageMessages.Content>()
                                                ?: PageMessages.Content()
                                        runCatching {
                                            content
                                        }.onSuccess { trySend(it) }
                                    }

                                    null -> {}
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

    suspend fun send(message: InsertMessage): Boolean {
        val action = sendText ?: return false
        runCatching {
            action.invoke(json().encodeToString(InsertMessage.serializer(), message))
        }
        return true
    }
}
