package com.wadii.screens.orders.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.OrderUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrdersViewModel(private val orderUseCase: OrderUseCase) : BaseViewModel<OrdersState, OrdersEvent>() {

    override val initialState: OrdersState
        get() = OrdersState()

    override val state: StateFlow<OrdersState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: OrdersEvent) {
        when (event) {
            OrdersEvent.Load -> load()
        }
    }

    private fun load() = screenModelScope.launch {
        orderUseCase.showAllOrderOfUser { response ->
            response.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(orders = data.data ?: emptyList(), isLoading = false) } },
                onError = { error, _ -> updateState { it.copy(error = error, isLoading = false) } }
            )
        }
    }
}
