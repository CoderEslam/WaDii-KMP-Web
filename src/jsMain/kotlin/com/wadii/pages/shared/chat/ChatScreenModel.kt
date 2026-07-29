package com.wadii.pages.shared.chat

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.core.toJson
import com.wadii.data.websocket.ChatWebSocketService
import com.wadii.domain.model.Paginator
import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.chat.InsertMessage
import com.wadii.domain.model.chat.PageMessages
import com.wadii.domain.usecase.MessageUseCase
import com.wadii.state.AppState
import com.wadii.utils.RequestState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messageUseCase: MessageUseCase,
    private val wsService: ChatWebSocketService
) : BaseViewModel<ChatState, ChatEvent>() {

    private val TAG = "ChatScreenModel"
    override val initialState: ChatState get() = ChatState()

    override val state: StateFlow<ChatState> = _state
        .onStart { chatList() }
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    private var paginator: Paginator<Int, PageMessages>? = null
    private var wsJob: Job? = null

    override fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SelectContact -> selectContact(event.contact)
            is ChatEvent.SetMessage -> updateState { it.copy(messageText = event.text) }
            is ChatEvent.Send -> send()
            is ChatEvent.LoadMoreMessages -> loadMoreMessages()
        }
    }

    private fun chatList() = screenModelScope.launch {
        messageUseCase.chatList { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState { it.copy(contacts = data.data, isLoading = false) }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun selectContact(contact: ChatContact) {
        updateState { it.copy(selectedContact = contact, messages = mutableListOf()) }
        paginator = buildPaginator(contact.contact.id)
        loadMoreMessages()
        connectWebSocket()
    }

    private fun connectWebSocket() {
        wsJob?.cancel()
        val token = AppState.token ?: return
        val userId = AppState.user?.id ?: return
        wsJob = screenModelScope.launch {
            wsService.connect(userId, token).collect { message ->
                updateState {
                    it.copy(messages = it.messages.toMutableList().apply {
                        add(message)
                    })
                }
            }
        }
    }

    private fun loadMoreMessages() = screenModelScope.launch {
        paginator?.loadNextItems()
    }

    private fun buildPaginator(contactId: Long) = Paginator<Int, PageMessages>(
        initialKey = 0,
        onLoadUpdated = { loading ->
            if (_state.value.messages.isEmpty()) {
                updateState { it.copy(messagesLoading = loading) }
            } else {
                updateState { it.copy(isLoadingMoreMessages = loading) }
            }
        },
        onRequest = { page ->
            var result: RequestState<PageMessages> = RequestState.Idle
            messageUseCase.conversation(contactId, page) { r ->
                when (r) {
                    is RequestState.Success -> result = RequestState.Success(r.data.data)
                    is RequestState.Error -> result = RequestState.Error(r.message, r.code)
                    else -> Unit
                }
            }
            result
        },
        getNextKey = { currentKey, _ -> currentKey + 1 },
        onError = { _ ->
            updateState { it.copy(messagesLoading = false, isLoadingMoreMessages = false) }
        },
        onSuccess = { result, _ ->
            updateState {
                it.copy(messages = result.content.toMutableList().apply {
                    addAll(it.messages)
                })
            }
        },
        endReached = { _, result -> result.last }
    )

    private fun send() {
        val current = _state.value
        val contact = current.selectedContact
        if (contact.contact.id == 0L || current.messageText.isBlank() || current.sending) return
        val text = current.messageText.trim()
        updateState { it.copy(messageText = "", sending = true) }
        screenModelScope.launch {
            val sentViaWs = wsService.send(
                InsertMessage(text = text, type = "text", toUserId = contact.contact.id)
            )
            if (sentViaWs) {
//                val optimistic = PageMessages.Content(
//                    text = text,
//                    type = "text",
//                    fromUser = AppState.user ?: User(),
//                    toUser = contact.contact
//                )
//                updateState {
//                    it.copy(
//                        sending = false,
//                        messages = it.messages.toMutableList().apply { add(optimistic) }
//                    )
//                }
                updateState { it.copy(sending = false, contacts = _state.value.contacts.toMutableList().apply {
                    this[this.indexOf(contact)] = contact.copy(
                        lastMessage = text,
                        lastMessageAt = "now"
                    )
                }) }
            } else {
                // WS not connected — fall back to REST and reload the conversation
                var restSent = false
                messageUseCase.insertMessage(
                    InsertMessage(text = text, type = "TEXT", toUserId = contact.contact.id)
                ) { r ->
                    r.handelState(
                        onLoading = {},
                        onSuccess = { _ -> restSent = true },
                        onError = { _, _ -> updateState { it.copy(sending = false) } }
                    )
                }
                if (restSent) {
                    updateState {
                        it.copy(
                            sending = false,
                            messages = mutableListOf()
                        )
                    }
                    paginator?.reset()
                    paginator?.loadNextItems()
                }
            }
        }
    }
}
