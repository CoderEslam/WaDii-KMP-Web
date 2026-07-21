package com.teacheronline.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentStudent(
    val id: Long = 0,
    val startDate: String = "",
    val endDate: String = "",
    val price: Double = 0.0,
    val notes: String? = null,
    val student: Student = Student(),
    val teacher: Teacher = Teacher(),
    val subject: Subject = Subject(),
    val accessValid: Boolean = false
)

@Serializable
data class PaymentStudentDto(
    val id: Long? = null,
    val price: Double = 0.0,
    val notes: String? = null,
    val studentId: Long = 0,
    val teacherId: Long = 0,
    val subjectId: Long = 0
)

@Serializable
data class PaymentStudentSubjectResponse(
    val id: Long = 0,
    val startDate: String = "",
    val endDate: String = "",
    val price: Double = 0.0,
    val notes: String? = null,
    val subjectName: String = "",
    val accessValid: Boolean = false
)
