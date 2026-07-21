package com.teacheronline.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AttendanceStatus { PRESENT, ABSENT, LATE }

@Serializable
data class Attendance(
    val id: Long = 0,
    val student: Student = Student(),
    val date: String = "",
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val subject: Subject = Subject(),
    val teacher: Teacher = Teacher()
)

@Serializable
data class AttendanceRequest(
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val studentId: Long = 0,
    val subjectId: Long = 0,
    val teacherId: Long = 0
)
