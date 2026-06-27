package com.wadii.domain.model.chat


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsertResponse(
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
) {
    @Serializable
    data class User(
        @SerialName("email")
        val email: String = "",
        @SerialName("fcmToken")
        val fcmToken: String = "",
        @SerialName("firstName")
        val firstName: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("image")
        val image: String? = null,
        @SerialName("lastName")
        val lastName: String = "",
        @SerialName("phone")
        val phone: String = "",
        @SerialName("role")
        val role: String = ""
    )
}