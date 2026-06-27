package com.wadii.screens.orders.detail

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiAcceptResponse
import com.wadii.data.api.apiCancelResponse
import com.wadii.data.api.apiGetOrder
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class OrderDetailScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<OrderDetailState>>(UiState.Loading)
        private set

    fun onEvent(event: OrderDetailEvent) = when (event) {
        is OrderDetailEvent.Load -> load(event.orderId)
        is OrderDetailEvent.AcceptResponse -> acceptResponse(event.responseId, event.orderId)
        is OrderDetailEvent.DeclineResponse -> declineResponse(event.responseId, event.orderId)
    }

    private fun load(orderId: Int) {
        screenModelScope.launch {
            state = UiState.Loading
            val order = apiGetOrder(orderId)
            state = if (order != null) UiState.Success(OrderDetailState(order))
            else UiState.Error("Order not found.")
        }
    }

    private fun acceptResponse(responseId: Long, orderId: Int) {
        screenModelScope.launch {
            if (apiAcceptResponse(responseId)) { AppState.toast("Response accepted!"); load(orderId) }
            else AppState.toast("Failed to accept", true)
        }
    }

    private fun declineResponse(responseId: Long, orderId: Int) {
        screenModelScope.launch {
            if (apiCancelResponse(responseId)) { AppState.toast("Response declined"); load(orderId) }
            else AppState.toast("Failed", true)
        }
    }
}
