package com.wadii.screens.orders.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.order.OrderCancelRequest
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.state.AppState
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
            is OrdersEvent.OpenCancelDialog -> updateState { it.copy(cancelingOrder = event.order, cancelReason = "") }
            OrdersEvent.DismissCancelDialog -> updateState { it.copy(cancelingOrder = null, cancelReason = "") }
            is OrdersEvent.SetCancelReason -> updateState { it.copy(cancelReason = event.value) }
            OrdersEvent.ConfirmCancel -> confirmCancel()
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

    private fun confirmCancel() = screenModelScope.launch {
        val order = _state.value.cancelingOrder ?: return@launch
        val reason = _state.value.cancelReason
        if (reason.isBlank()) {
            AppState.toast("Please provide a reason for cancelling", true)
            return@launch
        }
        orderUseCase.cancelOrder(OrderCancelRequest(orderId = order.id, reason = reason)) { response ->
            response.handelState(
                onLoading = { updateState { it.copy(isCancelling = true) } },
                onSuccess = {
                    AppState.toast("Order cancelled")
                    updateState {
                        it.copy(
                            orders = it.orders.filter { o -> o.id != order.id },
                            isCancelling = false,
                            cancelingOrder = null,
                            cancelReason = ""
                        )
                    }
                },
                onError = { _, _ ->
                    AppState.toast("Failed to cancel order", true)
                    updateState { it.copy(isCancelling = false) }
                }
            )
        }
    }
}
