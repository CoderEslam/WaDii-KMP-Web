package com.wadii.pages.shared.notifications

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.domain.usecase.NotificationUseCase
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val notificationUseCase: NotificationUseCase,
    private val marketplaceUseCase: MarketplaceUseCase,
    private val providerUseCase: ProviderUseCase
) : BaseViewModel<NotificationsState, NotificationsEvent>() {

    override val initialState: NotificationsState get() = NotificationsState()

    override val state: StateFlow<NotificationsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: NotificationsEvent) {
        when (event) {
            NotificationsEvent.Load -> load()
            is NotificationsEvent.MarkRead -> markRead(event.id)
            NotificationsEvent.MarkAllRead -> markAllRead()
            is NotificationsEvent.Delete -> delete(event.id)
            NotificationsEvent.RefreshRole -> refreshRole()
        }
    }

    /** The account becomes a seller on the server; fetch the shop so this session shows the seller screens. */
    private fun refreshRole() = screenModelScope.launch {
        providerUseCase.providerMe { r ->
            r.handelState(
                onSuccess = { data ->
                    AppState.saveUser(data.data.toLoginUser(), AppState.token ?: "")
                    updateState { it.copy(becameSeller = true) }
                },
                onError = { _, _ -> AppState.toast("Sign out and back in to start using your seller account") }
            )
        }
    }

    private fun load() = screenModelScope.launch {
        notificationUseCase.getNotifications { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                // Newest first, whatever order the server used.
                onSuccess = { data -> updateState { it.copy(notifications = data.data.sortedByDescending { n -> n.createdAt }, isLoading = false, error = null) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    // The list updates optimistically; a failed call puts the server's truth back by reloading.
    private fun markRead(id: Long) = screenModelScope.launch {
        updateState { s -> s.copy(notifications = s.notifications.map { if (it.id == id) it.copy(isRead = true) else it }) }
        marketplaceUseCase.markNotificationRead(id) { r ->
            r.handelState(onError = { e, _ -> AppState.toast(e, true); load() })
        }
    }

    private fun markAllRead() = screenModelScope.launch {
        updateState { s -> s.copy(notifications = s.notifications.map { it.copy(isRead = true) }) }
        marketplaceUseCase.markAllNotificationsRead { r ->
            r.handelState(onError = { e, _ -> AppState.toast(e, true); load() })
        }
    }

    private fun delete(id: Long) = screenModelScope.launch {
        updateState { s -> s.copy(notifications = s.notifications.filter { it.id != id }) }
        marketplaceUseCase.deleteNotification(id) { r ->
            r.handelState(onError = { e, _ -> AppState.toast(e, true); load() })
        }
    }
}
