package com.teacheronline.pages.admin.dashboard

data class AdminState(
    val isLoading: Boolean = false,
    val centersCount: Int = 0,
    val teachersCount: Int = 0,
    val subjectsCount: Int = 0,
    val studentsCount: Int = 0,
    val message: String = ""
)
