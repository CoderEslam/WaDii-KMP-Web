package com.wadii.screens.orders.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    private val orderId: Int,
    private val orderUseCase: OrderUseCase
) : BaseViewModel<OrderDetailState, OrderDetailEvent>() {

    override val initialState: OrderDetailState
        get() = OrderDetailState()

    override val state: StateFlow<OrderDetailState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: OrderDetailEvent) {
        when (event) {
            is OrderDetailEvent.Load -> load()
            is OrderDetailEvent.AcceptResponse -> AppState.toast("Accept coming soon")
            is OrderDetailEvent.DeclineResponse -> AppState.toast("Decline coming soon")
        }
    }

    private fun load() = screenModelScope.launch {
        orderUseCase.getOrderById(orderId) { response ->
            response.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(order = data.data, isLoading = false) } },
                onError = { error, _ -> updateState { it.copy(error = error, isLoading = false) } }
            )
        }
    }
}
