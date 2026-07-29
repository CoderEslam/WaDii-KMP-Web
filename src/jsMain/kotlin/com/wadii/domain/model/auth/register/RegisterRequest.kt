package com.wadii.domain.model.auth.register


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("email")
    val email: String = "",
    @SerialName("fcmToken")
    val fcmToken: String = "",
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("lastName")
    val lastName: String = "",
    @SerialName("password")
    val password: String = "",
    @SerialName("phone")
    val phone: String = "",
    @SerialName("userType")
    val userType: Int,
    @SerialName("cityId")
    val cityId: Long = 0
)