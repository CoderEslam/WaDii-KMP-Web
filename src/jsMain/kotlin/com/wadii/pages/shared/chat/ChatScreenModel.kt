package com.wadii.pages.shared.chat

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiGetChatList
import com.wadii.data.api.apiGetConversation
import com.wadii.data.api.apiSendMessage
import com.wadii.domain.model.chat.ChatContact
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class ChatScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<ChatState>>(UiState.Loading)
        private set

    init {
        onEvent(ChatEvent.Load)
    }

    fun onEvent(event: ChatEvent) = when (event) {
        ChatEvent.Load -> load()
        is ChatEvent.SelectContact -> selectContact(event.contact)
        is ChatEvent.SetMessage -> mutate { copy(messageText = event.text) }
        ChatEvent.Send -> send()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(ChatState(contacts = apiGetChatList()))
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

    private fun mutate(block: ChatState.() -> ChatState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
