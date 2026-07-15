package com.teacheronline.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    val id: Long = 0,
    val name: String = "",
    val price: Double = 0.0,
    val teachers: List<Teacher> = emptyList(),
    val students: List<Student> = emptyList()
)

@Serializable
data class SubjectRequest(
    val id: Long? = null,
    val name: String = "",
    val price: Double = 0.0
)
