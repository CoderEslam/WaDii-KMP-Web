package com.wadii.pages.provider.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiGetMyProvider
import com.wadii.data.api.apiGetProviderOrders
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class ProviderDashboardScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<ProviderDashboardState>>(UiState.Loading)
        private set

    init {
        onEvent(ProviderDashboardEvent.Load)
    }

    fun onEvent(event: ProviderDashboardEvent) = when (event) {
        ProviderDashboardEvent.Load -> load()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            val provider = apiGetMyProvider()
            val orders = apiGetProviderOrders()
            state = UiState.Success(ProviderDashboardState(provider, orders))
        }
    }
}
