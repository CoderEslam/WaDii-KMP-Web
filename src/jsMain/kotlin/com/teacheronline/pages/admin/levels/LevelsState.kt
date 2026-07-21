package com.teacheronline.pages.admin.levels

import com.teacheronline.domain.model.Level

data class LevelsState(
    val levels: List<Level> = emptyList(),
    val editingId: Long = 0,
    val name: String = "",
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
