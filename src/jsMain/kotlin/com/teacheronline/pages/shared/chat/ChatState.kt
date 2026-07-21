package com.teacheronline.pages.shared.chat

import com.teacheronline.domain.model.chat.ChatContact
import com.teacheronline.domain.model.chat.PageMessages

data class ChatState(
    val contacts: List<ChatContact> = emptyList(),
    val selectedContact: ChatContact = ChatContact(),
    val messages: MutableList<PageMessages.Content> = mutableListOf(),
    val messagesLoading: Boolean = false,
    val isLoadingMoreMessages: Boolean = false,
    val messageText: String = "",
    val sending: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = ""
)
