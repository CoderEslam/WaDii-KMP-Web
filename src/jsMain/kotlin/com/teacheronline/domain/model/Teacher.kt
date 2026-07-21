package com.teacheronline.domain.model

import com.teacheronline.domain.model.auth.login.User
import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val id: Long = 0,
    val phone: String = "",
    val user: User = User(),
    val subjects: List<Subject> = emptyList()
)

@Serializable
data class CreateSubjectsTeacher(
    val id: Long? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String? = null,
    val password: String? = null,
    val fcmToken: String? = null,
    val phone: String = "",
    val subjectIds: Set<Long> = emptySet()
)
