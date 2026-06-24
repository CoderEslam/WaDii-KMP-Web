package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiAcceptResponse
import com.wadii.api.apiCancelResponse
import com.wadii.api.apiGetOrder
import com.wadii.model.Order
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class OrderDetailEvent {
    data class Load(val orderId: Long) : OrderDetailEvent()
    data class AcceptResponse(val responseId: Long, val orderId: Long) : OrderDetailEvent()
    data class DeclineResponse(val responseId: Long, val orderId: Long) : OrderDetailEvent()
}

data class OrderDetailData(val order: Order)

class OrderDetailScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<OrderDetailData>>(UiState.Loading)
        private set

    fun onEvent(event: OrderDetailEvent) = when (event) {
        is OrderDetailEvent.Load -> load(event.orderId)
        is OrderDetailEvent.AcceptResponse -> acceptResponse(event.responseId, event.orderId)
        is OrderDetailEvent.DeclineResponse -> declineResponse(event.responseId, event.orderId)
    }

    private fun load(orderId: Long) {
        screenModelScope.launch {
            state = UiState.Loading
            val order = apiGetOrder(orderId)
            state = if (order != null) UiState.Success(OrderDetailData(order))
                    else UiState.Error("Order not found.")
        }
    }

    private fun acceptResponse(responseId: Long, orderId: Long) {
        screenModelScope.launch {
            if (apiAcceptResponse(responseId)) { AppState.toast("Response accepted!"); load(orderId) }
            else AppState.toast("Failed to accept", true)
        }
    }

    private fun declineResponse(responseId: Long, orderId: Long) {
        screenModelScope.launch {
            if (apiCancelResponse(responseId)) { AppState.toast("Response declined"); load(orderId) }
            else AppState.toast("Failed", true)
        }
    }
}
