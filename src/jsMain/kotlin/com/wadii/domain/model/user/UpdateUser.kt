package com.wadii.domain.model.user


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UpdateUser(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("lastName")
    val lastName: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("password")
    val password: String = "",
    @SerialName("fcmToken")
    val fcmToken: String = "",
    @SerialName("phone")
    val phone: String = "",
    @SerialName("userType")
    val userType: Long = 0,
    @SerialName("cityId")
    val cityId: Int = 0
)