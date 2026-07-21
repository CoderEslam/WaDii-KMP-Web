package com.teacheronline.pages.admin.secretaries

import com.teacheronline.domain.model.Secretary
import com.teacheronline.domain.model.Teacher

data class SecretariesState(
    val secretaries: List<Secretary> = emptyList(),
    val teachers: List<Teacher> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val teacherId: Long = 0,
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
