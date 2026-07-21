package com.teacheronline.data.websocket

import com.teacheronline.core.fromJson
import com.teacheronline.core.toJson
import com.teacheronline.data.api.json
import com.teacheronline.domain.model.chat.InsertMessage
import com.teacheronline.domain.model.chat.PageMessages
import com.teacheronline.domain.model.chat.Presence
import com.teacheronline.domain.model.chat.SocketEvent.*
import com.teacheronline.domain.model.chat.SocketResponse
import com.teacheronline.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatWebSocketService(private val client: HttpClient) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var sendText: (suspend (String) -> Unit)? = null
    private var connectionJob: Job? = null
    private var manuallyDisconnected = false
    private var currentUserId: Long? = null
    private var currentToken: String? = null

    private val _connectionState = MutableStateFlow(SocketConnectionState.DISCONNECTED)
    val connectionState: StateFlow<SocketConnectionState> = _connectionState.asStateFlow()

    private val _messages = MutableSharedFlow<PageMessages.Content>(extraBufferCapacity = 32)
    val messages: Flow<PageMessages.Content> = _messages

    fun connect(userId: Long, token: String): Flow<PageMessages.Content> {
        if (connectionJob?.isActive == true && currentUserId == userId && currentToken == token) {
            return _messages
        }
        currentUserId = userId
        currentToken = token
        manuallyDisconnected = false
        connectionJob?.cancel()
        connectionJob = scope.launch { runConnectionLoop(userId, token) }
        return _messages
    }

    fun disconnect() {
        manuallyDisconnected = true
        connectionJob?.cancel()
        connectionJob = null
        sendText = null
        _connectionState.value = SocketConnectionState.DISCONNECTED
    }

    fun reconnect() {
        val userId = currentUserId ?: return
        val token = currentToken ?: return
        manuallyDisconnected = false
        connectionJob?.cancel()
        connectionJob = scope.launch { runConnectionLoop(userId, token) }
    }

    private suspend fun runConnectionLoop(userId: Long, token: String) {
        var attempt = 0
        while (!manuallyDisconnected) {
            _connectionState.value = SocketConnectionState.CONNECTING
            runCatching {
                client.webSocket("${Constants.WS_URL}/$userId?token=$token") {
                    println("Connected to WS")
                    _connectionState.value = SocketConnectionState.CONNECTED
                    attempt = 0
                    sendText = { text -> send(Frame.Text(text)) }
                    try {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val socketResponse = frame.readText().fromJson<SocketResponse>()
                                when (socketResponse?.event) {
                                    PRESENCE.name -> {
                                        val presence =
                                            socketResponse.data.toJson().fromJson<Presence>()
                                    }

                                    MESSAGE.name -> {
                                        val content =
                                            socketResponse.data.toJson()
                                                .fromJson<PageMessages.Content>()
                                                ?: PageMessages.Content()
                                        _messages.tryEmit(content)
                                    }

                                    null -> {}
                                }
                            }
                        }
                    } finally {
                        sendText = null
                    }
                }
            }
            sendText = null

            if (manuallyDisconnected) {
                _connectionState.value = SocketConnectionState.DISCONNECTED
                return
            }

            _connectionState.value = SocketConnectionState.ERROR
            attempt++
            val backoff = (RECONNECT_BASE_DELAY_MS * (1L shl attempt.coerceAtMost(5)))
                .coerceAtMost(RECONNECT_MAX_DELAY_MS)
            delay(backoff)
        }
    }

    suspend fun send(message: InsertMessage): Boolean {
        val action = sendText ?: return false
        runCatching {
            action.invoke(json().encodeToString(InsertMessage.serializer(), message))
        }
        return true
    }

    private companion object {
        const val RECONNECT_BASE_DELAY_MS = 1_000L
        const val RECONNECT_MAX_DELAY_MS = 15_000L
    }
}

enum class SocketConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}
