package com.teacheronline.pages.teacher.secretaries

import com.teacheronline.domain.model.Secretary
import com.teacheronline.domain.model.Teacher

data class TeacherSecretariesState(
    val me: Teacher? = null,
    val mySecretaries: List<Secretary> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
