package com.wadii.pages.provider.orders

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiGetProviderOrders
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class ProviderOrdersScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<ProviderOrdersState>>(UiState.Loading)
        private set

    init {
        onEvent(ProviderOrdersEvent.Load)
    }

    fun onEvent(event: ProviderOrdersEvent) = when (event) {
        ProviderOrdersEvent.Load -> load()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(ProviderOrdersState(apiGetProviderOrders()))
        }
    }
}
