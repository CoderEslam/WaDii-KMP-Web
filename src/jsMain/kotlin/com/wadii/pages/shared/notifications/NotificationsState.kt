package com.wadii.pages.shared.notifications


data class NotificationsState(
    val notifications: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
