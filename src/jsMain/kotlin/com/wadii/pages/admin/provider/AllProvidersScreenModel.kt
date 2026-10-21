package com.wadii.pages.admin.provider

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AllProvidersViewModel(
    private val providerUseCase: ProviderUseCase,
    private val marketplaceUseCase: MarketplaceUseCase
) : BaseViewModel<AllProvidersState, AllProvidersEvent>() {

    override val initialState: AllProvidersState get() = AllProvidersState()

    override val state: StateFlow<AllProvidersState> = _state
        .onStart { load() }
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    override fun onEvent(event: AllProvidersEvent) {
        when (event) {
            AllProvidersEvent.Load -> load()
            is AllProvidersEvent.SetActive -> setActive(event.providerId, event.active)
        }
    }

    private fun setActive(id: Long, active: Boolean) = screenModelScope.launch {
        if (_state.value.busyProviderId != null) return@launch
        updateState { it.copy(busyProviderId = id) }
        marketplaceUseCase.setProviderActive(id, active) { r ->
            r.handelState(
                onSuccess = { data ->
                    updateState { s -> s.copy(busyProviderId = null, providers = s.providers.map { p -> if (p.id == id) p.copy(active = data.data.active) else p }) }
                    AppState.toast(if (active) "Provider reactivated" else "Provider suspended: no new orders will reach them")
                },
                onError = { error, _ ->
                    AppState.toast(error.ifBlank { "Couldn't change the provider status" }, true)
                    updateState { it.copy(busyProviderId = null) }
                }
            )
        }
    }

    private fun load() = screenModelScope.launch {
        providerUseCase.providersList { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            providers = data.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }
}
