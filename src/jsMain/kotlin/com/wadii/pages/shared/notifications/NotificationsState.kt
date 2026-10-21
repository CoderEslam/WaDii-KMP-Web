package com.wadii.pages.shared.notifications

import com.wadii.domain.model.notification.UserNotification


data class NotificationsState(
    val notifications: List<UserNotification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    /** Set once an accepted seller application has refreshed this session, so the screen can open the dashboard. */
    val becameSeller: Boolean = false
)
