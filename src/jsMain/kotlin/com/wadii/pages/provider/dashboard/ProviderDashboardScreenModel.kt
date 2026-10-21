package com.wadii.pages.provider.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.state.AppState
import com.wadii.domain.usecase.ProviderUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProviderDashboardViewModel(
    private val providerUseCase: ProviderUseCase,
    private val orderUseCase: OrderUseCase,
    private val marketplaceUseCase: MarketplaceUseCase
) : BaseViewModel<ProviderDashboardState, ProviderDashboardEvent>() {

    override val initialState: ProviderDashboardState get() = ProviderDashboardState()

    override val state: StateFlow<ProviderDashboardState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ProviderDashboardEvent) {
        when (event) {
            ProviderDashboardEvent.Load -> load()
            is ProviderDashboardEvent.SetAccepting -> setAccepting(event.accepting)
        }
    }

    /** POST /providers/availability: pause or resume new orders without being suspended. */
    private fun setAccepting(accepting: Boolean) = screenModelScope.launch {
        if (_state.value.savingAvailability) return@launch
        updateState { it.copy(savingAvailability = true) }
        marketplaceUseCase.setAvailability(accepting) { r ->
            r.handelState(
                onSuccess = { data ->
                    updateState { it.copy(provider = it.provider?.copy(acceptingOrders = data.data.acceptingOrders), savingAvailability = false) }
                    AppState.toast(if (accepting) "You're back: new orders will reach you" else "Paused: you won't get new orders until you switch this on")
                },
                onError = { error, _ ->
                    AppState.toast(error.ifBlank { "Couldn't change your availability" }, true)
                    updateState { it.copy(savingAvailability = false) }
                }
            )
        }
    }

    private fun load() = screenModelScope.launch {
        updateState { it.copy(isLoading = true, error = null) }
        providerUseCase.providerMe { r ->
            r.handelState(
                onLoading = {},
                onSuccess = { data -> updateState { it.copy(provider = data.data) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
        orderUseCase.showAllOrderOfProvider { r ->
            r.handelState(
                onLoading = {},
                onSuccess = { data -> updateState { it.copy(orders = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }
}
