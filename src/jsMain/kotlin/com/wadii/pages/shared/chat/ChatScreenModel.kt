package com.wadii.pages.shared.chat

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.chat.InsertMessage
import com.wadii.domain.usecase.MessageUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messageUseCase: MessageUseCase
) : BaseViewModel<ChatState, ChatEvent>() {

    override val initialState: ChatState get() = ChatState()

    override val state: StateFlow<ChatState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ChatEvent) {
        when (event) {
            ChatEvent.Load -> load()
            is ChatEvent.SelectContact -> selectContact(event.contact)
            is ChatEvent.SetMessage -> updateState { it.copy(messageText = event.text) }
            ChatEvent.Send -> send()
        }
    }

    private fun load() = screenModelScope.launch {
        messageUseCase.chatList { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            contacts = data.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun selectContact(contact: ChatContact) =
        screenModelScope.launch {
            messageUseCase.conversation(contact.contact.id, 0) { r ->
                r.handelState(
                    onLoading = {
                        updateState { it.copy(messagesLoading = true) }
                    },
                    onSuccess = { data ->
                        updateState {
                            it.copy(
                                messages = data.data?.content ?: emptyList(),
                                messagesLoading = false
                            )
                        }
                    },
                    onError = { _, _ -> updateState { it.copy(messagesLoading = false) } }
                )
            }
        }


    private fun send() {
        val current = _state.value
        val contact = current.selectedContact ?: return
        if (current.messageText.isBlank() || current.sending) return
        val text = current.messageText.trim()
        updateState { it.copy(messageText = "", sending = true) }
        screenModelScope.launch {
            var sent = false
            messageUseCase.insertMessage(
                InsertMessage(
                    text = text,
                    type = "TEXT",
                    toUserId = contact.contact.id
                )
            ) { r ->
                r.handelState(
                    onLoading = {},
                    onSuccess = { _ -> sent = true },
                    onError = { _, _ -> updateState { it.copy(sending = false) } }
                )
            }
            if (sent) {
                messageUseCase.conversation(contact.contact.id, 0) { r ->
                    r.handelState(
                        onLoading = {},
                        onSuccess = { data ->
                            updateState {
                                it.copy(
                                    messages = data.data?.content ?: emptyList(), sending = false
                                )
                            }
                        },
                        onError = { _, _ -> updateState { it.copy(sending = false) } }
                    )
                }
            }
        }
    }
}
