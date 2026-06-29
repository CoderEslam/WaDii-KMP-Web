package com.wadii.pages.shared.chat

import com.wadii.domain.model.chat.ChatContact

sealed class ChatEvent {
    data class SelectContact(val contact: ChatContact) : ChatEvent()
    data class SetMessage(val text: String) : ChatEvent()
    object Send : ChatEvent()
    object LoadMoreMessages : ChatEvent()
}
