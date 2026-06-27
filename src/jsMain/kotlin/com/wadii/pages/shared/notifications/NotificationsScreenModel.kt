package com.wadii.pages.shared.notifications

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiDeleteNotification
import com.wadii.data.api.apiGetNotifications
import com.wadii.data.api.apiMarkAllRead
import com.wadii.data.api.apiMarkRead
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class NotificationsScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<NotificationsState>>(UiState.Loading)
        private set

    init {
        onEvent(NotificationsEvent.Load)
    }

    fun onEvent(event: NotificationsEvent) = when (event) {
        NotificationsEvent.Load -> load()
        is NotificationsEvent.MarkRead -> markRead(event.id)
        NotificationsEvent.MarkAllRead -> markAllRead()
        is NotificationsEvent.Delete -> delete(event.id)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(NotificationsState(apiGetNotifications()))
        }
    }

    private fun markRead(id: Long) {
        screenModelScope.launch {
//            apiMarkRead(id)
//            mutate { copy(notifications = notifications.map { if (it.id == id) it.copy(isRead = true) else it }) }
        }
    }

    private fun markAllRead() {
        screenModelScope.launch {
//            if (apiMarkAllRead()) {
//                mutate { copy(notifications = notifications.map { it.copy(isRead = true) }) }
//                AppState.toast("All marked as read")
//            }
        }
    }

    private fun delete(id: Long) {
        screenModelScope.launch {
//            if (apiDeleteNotification(id))
//                mutate { copy(notifications = notifications.filter { it.id != id }) }
        }
    }

    private fun mutate(block: NotificationsState.() -> NotificationsState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
