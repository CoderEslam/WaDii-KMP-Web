package com.teacheronline.domain.model.auth

import kotlinx.serialization.Serializable

/**
 * Shared body for /auth/register and /auth/login (doc §2). `role` is accepted but ignored
 * server-side on register — every new account is created as ADMIN regardless of its value.
 */
@Serializable
data class AuthRequest(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val fcmToken: String = "",
    val password: String = "",
    val role: Int = 0
)
