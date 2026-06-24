package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiGetChatList
import com.wadii.api.apiGetConversation
import com.wadii.api.apiSendMessage
import com.wadii.model.ChatContact
import com.wadii.model.Message
import kotlinx.coroutines.launch

sealed class ChatEvent {
    object Load : ChatEvent()
    data class SelectContact(val contact: ChatContact) : ChatEvent()
    data class SetMessage(val text: String) : ChatEvent()
    object Send : ChatEvent()
}

data class ChatData(
    val contacts: List<ChatContact> = emptyList(),
    val selectedContact: ChatContact? = null,
    val messages: List<Message> = emptyList(),
    val messagesLoading: Boolean = false,
    val messageText: String = "",
    val sending: Boolean = false
)

class ChatScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<ChatData>>(UiState.Loading)
        private set

    init { onEvent(ChatEvent.Load) }

    fun onEvent(event: ChatEvent) = when (event) {
        ChatEvent.Load -> load()
        is ChatEvent.SelectContact -> selectContact(event.contact)
        is ChatEvent.SetMessage -> mutate { copy(messageText = event.text) }
        ChatEvent.Send -> send()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(ChatData(contacts = apiGetChatList()))
        }
    }

    private fun selectContact(contact: ChatContact) {
        mutate { copy(selectedContact = contact, messagesLoading = true, messages = emptyList()) }
        screenModelScope.launch {
            val msgs = apiGetConversation(contact.id)
            mutate { copy(messages = msgs, messagesLoading = false) }
        }
    }

    private fun send() {
        val d = (state as? UiState.Success)?.data ?: return
        val contact = d.selectedContact ?: return
        if (d.messageText.isBlank() || d.sending) return
        val text = d.messageText.trim()
        mutate { copy(messageText = "", sending = true) }
        screenModelScope.launch {
            apiSendMessage(contact.id, text)
            val msgs = apiGetConversation(contact.id)
            mutate { copy(messages = msgs, sending = false) }
        }
    }

    private fun mutate(block: ChatData.() -> ChatData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
