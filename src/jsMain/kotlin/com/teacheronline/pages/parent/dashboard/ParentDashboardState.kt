package com.teacheronline.pages.parent.dashboard

import com.teacheronline.domain.model.PaymentStudentSubjectResponse
import com.teacheronline.domain.model.Student

data class ParentDashboardState(
    val children: List<Student> = emptyList(),
    val statusByStudentId: Map<Long, List<PaymentStudentSubjectResponse>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)
