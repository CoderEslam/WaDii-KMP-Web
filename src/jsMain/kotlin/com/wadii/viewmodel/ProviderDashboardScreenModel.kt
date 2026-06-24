package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiGetMyProvider
import com.wadii.api.apiGetProviderOrders
import com.wadii.model.Order
import com.wadii.model.Provider
import kotlinx.coroutines.launch

sealed class ProviderDashboardEvent {
    object Load : ProviderDashboardEvent()
}

data class ProviderDashboardData(val provider: Provider?, val orders: List<Order>)

class ProviderDashboardScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<ProviderDashboardData>>(UiState.Loading)
        private set

    init { onEvent(ProviderDashboardEvent.Load) }

    fun onEvent(event: ProviderDashboardEvent) = when (event) {
        ProviderDashboardEvent.Load -> load()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            val provider = apiGetMyProvider()
            val orders = apiGetProviderOrders()
            state = UiState.Success(ProviderDashboardData(provider, orders))
        }
    }
}
