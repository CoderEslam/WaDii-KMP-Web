package com.wadii.pages.provider.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.domain.usecase.ProviderUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProviderDashboardViewModel(
    private val providerUseCase: ProviderUseCase,
    private val orderUseCase: OrderUseCase
) : BaseViewModel<ProviderDashboardState, ProviderDashboardEvent>() {

    override val initialState: ProviderDashboardState get() = ProviderDashboardState()

    override val state: StateFlow<ProviderDashboardState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ProviderDashboardEvent) {
        when (event) {
            ProviderDashboardEvent.Load -> load()
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
