package com.wadii.domain.model.auth.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String = "",
    val password: String = "",
    val fcmToken: String = "",
    val userType: Int = 0
)
