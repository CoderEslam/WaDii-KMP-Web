package com.teacheronline.domain.model

import com.teacheronline.domain.model.auth.login.User
import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: Long = 0,
    val parentId: Long = 0,
    val parentContact: String = "",
    val phone: String = "",
    val user: User = User(),
    val subjects: List<Subject> = emptyList(),
    val level: Level = Level(),
    val schedules: List<Schedule> = emptyList(),
    val educationalCenters: List<EducationalCenter> = emptyList(),
    val exams: List<Exam> = emptyList()
)

// ExamController exists as an empty stub server-side (doc §18) — no documented fields yet.
@Serializable
data class Exam(val id: Long = 0)

@Serializable
data class StudentRequest(
    val parentId: Long = 0,
    val parentContact: String = "",
    val phone: String = "",
    val subjectIds: Set<Long> = emptySet(),
    val scheduleIds: Set<Long> = emptySet(),
    val levelId: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val fcmToken: String = ""
)
