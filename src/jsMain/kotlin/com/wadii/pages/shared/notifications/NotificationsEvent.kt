package com.wadii.pages.shared.notifications

sealed class NotificationsEvent {
    object Load : NotificationsEvent()
    data class MarkRead(val id: Long) : NotificationsEvent()
    object MarkAllRead : NotificationsEvent()
    data class Delete(val id: Long) : NotificationsEvent()
}
