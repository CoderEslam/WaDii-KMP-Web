package com.teacheronline.pages.teacher.dashboard

import com.teacheronline.domain.model.Teacher

data class TeacherDashboardState(
    val me: Teacher? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
