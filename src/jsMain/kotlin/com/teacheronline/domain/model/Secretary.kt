package com.teacheronline.domain.model

import com.teacheronline.domain.model.auth.login.User
import kotlinx.serialization.Serializable

@Serializable
data class Secretary(
    val id: Long = 0,
    val phone: String = "",
    val user: User = User(),
    val teacher: Teacher = Teacher()
)

@Serializable
data class SecretaryDto(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val fcmToken: String = "",
    val phone: String = "",
    val teacherId: Long = 0
)
