package com.teacheronline.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val id: Long = 0,
    val dayOfWeek: String = "",
    val timeSlotStart: String = "",
    val timeSlotEnd: String = ""
)

@Serializable
data class ScheduleDto(
    val dayOfWeek: String = "",
    val timeSlotStart: String = "",
    val timeSlotEnd: String = "",
    val teacherId: Long = 0,
    val subjectId: Long = 0
)

// Body for the GET /schedule/count request (doc §14 — a GET with a JSON body).
@Serializable
data class AttendanceMonth(
    val subjectId: Long = 0,
    val teacherId: Long = 0,
    val startMonth: Int = 1,
    val endMonth: Int = 12
)
