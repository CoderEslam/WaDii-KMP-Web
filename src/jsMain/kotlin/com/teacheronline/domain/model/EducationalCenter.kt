package com.teacheronline.domain.model

import com.teacheronline.domain.model.auth.login.User
import kotlinx.serialization.Serializable

@Serializable
data class EducationalCenter(
    val id: Long = 0,
    val name: String = "",
    val address: String = "",
    val contactInfo: String = "",
    val users: List<User> = emptyList(),
    val students: List<Student> = emptyList()
)

@Serializable
data class EducationalCenterDto(
    val id: Long? = null,
    val name: String = "",
    val address: String = "",
    val contactInfo: String = ""
)
