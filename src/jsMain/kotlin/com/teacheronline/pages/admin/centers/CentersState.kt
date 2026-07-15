package com.teacheronline.pages.admin.centers

import com.teacheronline.domain.model.EducationalCenter

data class CentersState(
    val centers: List<EducationalCenter> = emptyList(),
    val editingId: Long = 0,
    val name: String = "",
    val address: String = "",
    val contactInfo: String = "",
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
