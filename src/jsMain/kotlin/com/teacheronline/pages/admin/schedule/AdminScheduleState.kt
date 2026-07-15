package com.teacheronline.pages.admin.schedule

import com.teacheronline.domain.model.Schedule
import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.model.Teacher

data class AdminScheduleState(
    val teachers: List<Teacher> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val createdThisSession: List<Schedule> = emptyList(),
    val dayOfWeek: String = "Monday",
    val timeSlotStart: String = "09:00:00",
    val timeSlotEnd: String = "10:00:00",
    val teacherId: Long = 0,
    val subjectId: Long = 0,
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
