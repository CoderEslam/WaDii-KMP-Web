package com.teacheronline.pages.student.dashboard

import com.teacheronline.domain.model.Attendance
import com.teacheronline.domain.model.PaymentStudentSubjectResponse
import com.teacheronline.domain.model.Student

data class StudentDashboardState(
    val me: Student? = null,
    val subjectsStatus: List<PaymentStudentSubjectResponse> = emptyList(),
    val attendanceHistory: List<Attendance> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
