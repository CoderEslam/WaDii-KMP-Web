package com.teacheronline.pages.admin.languages

import com.teacheronline.domain.model.Language

data class LanguagesState(
    val languages: List<Language> = emptyList(),
    val editingId: Long = 0,
    val name: String = "",
    val prefix: String = "",
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
