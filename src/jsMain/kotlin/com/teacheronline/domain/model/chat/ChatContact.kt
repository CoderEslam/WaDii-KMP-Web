package com.teacheronline.domain.model.chat


import com.teacheronline.domain.model.auth.login.User
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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as ChatContact

        if (id != other.id) return false
        if (contact != other.contact) return false
        if (user != other.user) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + contact.hashCode()
        result = 31 * result + user.hashCode()
        return result
    }

}
