package com.wadii.pages.shared.notifications

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.NotificationUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val notificationUseCase: NotificationUseCase
) : BaseViewModel<NotificationsState, NotificationsEvent>() {

    override val initialState: NotificationsState get() = NotificationsState()

    override val state: StateFlow<NotificationsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: NotificationsEvent) {
        when (event) {
            NotificationsEvent.Load -> load()
            is NotificationsEvent.MarkRead -> Unit
            NotificationsEvent.MarkAllRead -> Unit
            is NotificationsEvent.Delete -> Unit
        }
    }

    private fun load() = screenModelScope.launch {
        notificationUseCase.getNotifications { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(notifications = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }
}
