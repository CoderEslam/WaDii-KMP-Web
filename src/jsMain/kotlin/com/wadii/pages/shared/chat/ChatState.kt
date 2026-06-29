package com.wadii.pages.shared.chat

import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.chat.PageMessages

data class ChatState(
    val contacts: List<ChatContact> = emptyList(),
    val selectedContact: ChatContact = ChatContact(),
    val messages: List<PageMessages.Content> = emptyList(),
    val messagesLoading: Boolean = false,
    val messageText: String = "",
    val sending: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = ""
)
