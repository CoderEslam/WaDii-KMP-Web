package com.teacheronline.pages.teacher.schedule

import com.teacheronline.domain.model.Schedule
import com.teacheronline.domain.model.Teacher

data class TeacherScheduleState(
    val me: Teacher? = null,
    val dayOfWeek: String = "Monday",
    val timeSlotStart: String = "09:00:00",
    val timeSlotEnd: String = "10:00:00",
    val subjectId: Long = 0,
    val createdThisSession: List<Schedule> = emptyList(),
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
