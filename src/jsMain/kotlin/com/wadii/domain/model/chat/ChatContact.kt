package com.wadii.domain.model.chat


import com.wadii.domain.model.auth.login.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatContact(
    @SerialName("contact")
    val contact: User = User(),
    @SerialName("id")
    val id: Int = 0,
    @SerialName("lastMessageAt")
    val lastMessageAt: String = "",
    @SerialName("user")
    val user: User = User(),
    @SerialName("lastMessage")
    val lastMessage: String = "",
    @SerialName("messageType")
    val messageType: String = "",
    @SerialName("unreadCount")
    val unreadCount: Int = 0
) {

}
