package com.wadii.screens.providerDetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProviderDetailViewModel(
    private val providerId: Int,
    private val providerUseCase: ProviderUseCase
) : BaseViewModel<ProviderDetailState, ProviderDetailEvent>() {

    override val initialState: ProviderDetailState
        get() = ProviderDetailState()

    override val state: StateFlow<ProviderDetailState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ProviderDetailEvent) {
        when (event) {
            is ProviderDetailEvent.Load -> load()
            is ProviderDetailEvent.ToggleFollow -> toggleFollow(event.providerId)
        }
    }

    private fun load() = screenModelScope.launch {
        providerUseCase.getProviderById(providerId) { response ->
            response.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    println("getProviderById: $data")
                    updateState {
                        it.copy(
                            provider = data.data,
                            isLoading = false,
                            following = AppState.user?.id != null && data.data.followers.any { it.user.id == AppState.user?.id }
                        )
                    }
                },
                onError = { error, _ ->
                    updateState {
                        it.copy(
                            error = error,
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    private fun toggleFollow(id: Int) = screenModelScope.launch {
        val following = _state.value.following
        if (following) {
            providerUseCase.unfollowProvider(id) { response ->
                response.handelState(
                    onLoading = {},
                    onSuccess = { _ ->
                        updateState { it.copy(following = false) };
                        AppState.toast("Unfollowed")
                    },
                    onError = { _, _ -> }
                )
            }
        } else {
            providerUseCase.followProvider(id) { response ->
                response.handelState(
                    onLoading = {},
                    onSuccess = { _ ->
                        updateState { it.copy(following = true) };
                        AppState.toast("Following!")
                    },
                    onError = { _, _ -> }
                )
            }
        }
    }
}
