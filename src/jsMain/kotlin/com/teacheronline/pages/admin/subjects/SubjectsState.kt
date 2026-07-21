package com.teacheronline.pages.admin.subjects

import com.teacheronline.domain.model.Subject

data class SubjectsState(
    val subjects: List<Subject> = emptyList(),
    val editingId: Long = 0,
    val name: String = "",
    val price: String = "",
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
