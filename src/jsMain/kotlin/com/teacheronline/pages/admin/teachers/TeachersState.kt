package com.teacheronline.pages.admin.teachers

import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.model.Teacher

data class TeachersState(
    val teachers: List<Teacher> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val editingId: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val selectedSubjectIds: Set<Long> = emptySet(),
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
