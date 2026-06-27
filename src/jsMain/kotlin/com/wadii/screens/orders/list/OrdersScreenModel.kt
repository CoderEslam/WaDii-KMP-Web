package com.wadii.screens.orders.list

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiGetUserOrders
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class OrdersScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<OrdersState>>(UiState.Loading)
        private set

    init {
        onEvent(OrdersEvent.Load)
    }

    fun onEvent(event: OrdersEvent) = when (event) {
        OrdersEvent.Load -> load()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(OrdersState(apiGetUserOrders()))
        }
    }
}
