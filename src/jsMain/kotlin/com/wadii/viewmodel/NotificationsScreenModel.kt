package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.*
import com.wadii.model.UserNotification
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class NotificationsEvent {
    object Load : NotificationsEvent()
    data class MarkRead(val id: Long) : NotificationsEvent()
    object MarkAllRead : NotificationsEvent()
    data class Delete(val id: Long) : NotificationsEvent()
}

data class NotificationsData(val notifications: List<UserNotification>)

class NotificationsScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<NotificationsData>>(UiState.Loading)
        private set

    init { onEvent(NotificationsEvent.Load) }

    fun onEvent(event: NotificationsEvent) = when (event) {
        NotificationsEvent.Load -> load()
        is NotificationsEvent.MarkRead -> markRead(event.id)
        NotificationsEvent.MarkAllRead -> markAllRead()
        is NotificationsEvent.Delete -> delete(event.id)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(NotificationsData(apiGetNotifications()))
        }
    }

    private fun markRead(id: Long) {
        screenModelScope.launch {
            apiMarkRead(id)
            mutate { copy(notifications = notifications.map { if (it.id == id) it.copy(isRead = true) else it }) }
        }
    }

    private fun markAllRead() {
        screenModelScope.launch {
            if (apiMarkAllRead()) {
                mutate { copy(notifications = notifications.map { it.copy(isRead = true) }) }
                AppState.toast("All marked as read")
            }
        }
    }

    private fun delete(id: Long) {
        screenModelScope.launch {
            if (apiDeleteNotification(id))
                mutate { copy(notifications = notifications.filter { it.id != id }) }
        }
    }

    private fun mutate(block: NotificationsData.() -> NotificationsData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
