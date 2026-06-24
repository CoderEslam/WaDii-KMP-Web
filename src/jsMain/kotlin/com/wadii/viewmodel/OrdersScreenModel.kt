package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiGetUserOrders
import com.wadii.model.Order
import kotlinx.coroutines.launch

sealed class OrdersEvent {
    object Load : OrdersEvent()
}

data class OrdersData(val orders: List<Order>)

class OrdersScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<OrdersData>>(UiState.Loading)
        private set

    init { onEvent(OrdersEvent.Load) }

    fun onEvent(event: OrdersEvent) = when (event) {
        OrdersEvent.Load -> load()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(OrdersData(apiGetUserOrders()))
        }
    }
}
