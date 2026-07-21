package com.teacheronline.pages.admin.config

data class ConfigState(
    val key: String = "attendance_time_range",
    val currentValue: Int? = null,
    val newValue: String = "",
    val isLoading: Boolean = false,
    val saving: Boolean = false,
    val error: String? = null
)
