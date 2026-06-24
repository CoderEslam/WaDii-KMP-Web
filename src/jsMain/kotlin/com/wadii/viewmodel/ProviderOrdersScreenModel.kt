package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiGetProviderOrders
import com.wadii.model.Order
import kotlinx.coroutines.launch

sealed class ProviderOrdersEvent {
    object Load : ProviderOrdersEvent()
}

data class ProviderOrdersData(val orders: List<Order>)

class ProviderOrdersScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<ProviderOrdersData>>(UiState.Loading)
        private set

    init { onEvent(ProviderOrdersEvent.Load) }

    fun onEvent(event: ProviderOrdersEvent) = when (event) {
        ProviderOrdersEvent.Load -> load()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(ProviderOrdersData(apiGetProviderOrders()))
        }
    }
}
