package com.wadii.pages.provider.orders

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.OrderUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProviderOrdersViewModel(
    private val orderUseCase: OrderUseCase
) : BaseViewModel<ProviderOrdersState, ProviderOrdersEvent>() {

    override val initialState: ProviderOrdersState get() = ProviderOrdersState()

    override val state: StateFlow<ProviderOrdersState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ProviderOrdersEvent) {
        when (event) {
            ProviderOrdersEvent.Load -> load()
        }
    }

    private fun load() = screenModelScope.launch {
        orderUseCase.showAllOrderOfProvider { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(orders = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }
}
