package com.teacheronline.domain.model.chat

import com.teacheronline.domain.model.auth.login.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowAllMessagesResponse(
    @SerialName("createdAt")
    val createdAt: String = "",
    @SerialName("fromUser")
    val fromUser: User = User(),
    @SerialName("id")
    val id: Int = 0,
    @SerialName("text")
    val text: String = "",
    @SerialName("toUser")
    val toUser: User = User(),
    @SerialName("type")
    val type: String = ""
)
