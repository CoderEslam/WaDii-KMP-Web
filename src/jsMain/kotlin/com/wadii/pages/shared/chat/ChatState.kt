package com.wadii.pages.shared.chat

import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.chat.ShowAllMessagesResponse

data class ChatState(
    val contacts: List<ChatContact> = emptyList(),
    val selectedContact: ChatContact? = null,
    val messages: List<ShowAllMessagesResponse> = emptyList(),
    val messagesLoading: Boolean = false,
    val messageText: String = "",
    val sending: Boolean = false
)
