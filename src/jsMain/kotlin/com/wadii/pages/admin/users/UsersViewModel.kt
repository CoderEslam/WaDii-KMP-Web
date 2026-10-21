package com.wadii.pages.admin.users

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UsersState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = "",
    /** User whose action is running, so only that row shows a spinner. */
    val busyUserId: Long? = null,
    val deletingUser: User? = null
)

sealed class UsersEvent {
    object Load : UsersEvent()
    data class SetQuery(val value: String) : UsersEvent()
    data class MakeBuyer(val user: User) : UsersEvent()
    data class MakeSeller(val user: User) : UsersEvent()
    data class AskDelete(val user: User) : UsersEvent()
    object DismissDelete : UsersEvent()
    object ConfirmDelete : UsersEvent()
}

/** Admin: every account, role switches (provider-requests put-it-*) and removal. */
class UsersViewModel(
    private val marketplaceUseCase: MarketplaceUseCase,
    private val providerUseCase: ProviderUseCase
) : BaseViewModel<UsersState, UsersEvent>() {

    override val initialState: UsersState get() = UsersState()

    override val state: StateFlow<UsersState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: UsersEvent) {
        when (event) {
            UsersEvent.Load -> load()
            is UsersEvent.SetQuery -> updateState { it.copy(query = event.value) }
            is UsersEvent.MakeBuyer -> switchRole(event.user, toSeller = false)
            is UsersEvent.MakeSeller -> switchRole(event.user, toSeller = true)
            is UsersEvent.AskDelete -> updateState { it.copy(deletingUser = event.user) }
            UsersEvent.DismissDelete -> updateState { it.copy(deletingUser = null) }
            UsersEvent.ConfirmDelete -> delete()
        }
    }

    private fun load() = screenModelScope.launch {
        marketplaceUseCase.getAllUsers { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = it.users.isEmpty()) } },
                onSuccess = { data -> updateState { it.copy(users = data.data, isLoading = false, error = null) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    /** POST /provider-requests/put-it-user|put-it-provider/{userId}: flips the role, nothing is deleted. */
    private fun switchRole(user: User, toSeller: Boolean) = screenModelScope.launch {
        if (_state.value.busyUserId != null) return@launch
        updateState { it.copy(busyUserId = user.id) }
        val handle: (com.wadii.utils.RequestState<com.wadii.domain.model.BaseResponse<User>>) -> Unit = { r ->
            r.handelState(
                onSuccess = { data ->
                    updateState { s -> s.copy(busyUserId = null, users = s.users.map { if (it.id == user.id) data.data else it }) }
                    // "put-it-provider" answers 200 with the role unchanged when the user has no provider profile.
                    if (toSeller && data.data.role != "PROVIDER") {
                        AppState.toast("${user.firstName} has no seller profile yet. Create one from Providers, or approve their application.", true)
                    } else {
                        AppState.toast(if (toSeller) "${user.firstName} is a seller again" else "${user.firstName} is now a buyer")
                    }
                },
                onError = { error, _ ->
                    AppState.toast(error.ifBlank { "Couldn't change the role" }, true)
                    updateState { it.copy(busyUserId = null) }
                }
            )
        }
        if (toSeller) providerUseCase.putItProvider(user.id, handle) else providerUseCase.putItUser(user.id, handle)
    }

    private fun delete() = screenModelScope.launch {
        val user = _state.value.deletingUser ?: return@launch
        if (user.id == AppState.user?.id) {
            AppState.toast("You can't delete your own account here", true)
            updateState { it.copy(deletingUser = null) }
            return@launch
        }
        updateState { it.copy(busyUserId = user.id) }
        marketplaceUseCase.deleteAccount(user.id) { r ->
            r.handelState(
                onSuccess = {
                    AppState.toast("Account deleted")
                    updateState { s -> s.copy(busyUserId = null, deletingUser = null, users = s.users.filter { it.id != user.id }) }
                },
                onError = { error, _ ->
                    // 409: the account still owns orders, responses or a shop.
                    AppState.toast(error.ifBlank { "Couldn't delete the account" }, true)
                    updateState { it.copy(busyUserId = null, deletingUser = null) }
                }
            )
        }
    }
}
