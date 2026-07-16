package com.teacheronline.domain.model

import com.teacheronline.domain.model.auth.login.User
import kotlinx.serialization.Serializable

@Serializable
data class Parent(
    val id: Long = 0,
    val parentContact: String = "",
    val phone: String = "",
    val user: User = User(),
    val students: List<Student> = emptyList()
)

@Serializable
data class ParentRequest(
    val parentContact: String = "",
    val phone: String = "",
    val studentIds: Set<Long>? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val fcmToken: String = ""
)

// POST /parents/enroll — creates the parent's login + record, then creates each `students`
// entry as a brand-new student account attached to that new parent, all in one transaction.
@Serializable
data class EnrollRequest(
    val parentContact: String = "",
    val phone: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val fcmToken: String = "",
    val students: List<StudentRequest> = emptyList()
)
