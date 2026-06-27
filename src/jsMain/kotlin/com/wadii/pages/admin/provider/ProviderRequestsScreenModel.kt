package com.wadii.pages.admin.provider

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiAcceptRequest
import com.wadii.data.api.apiGetAllRequests
import com.wadii.domain.model.provider.ProviderRequest
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class ProviderRequestsScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<ProviderRequestsState>>(UiState.Loading)
        private set

    init {
        onEvent(ProviderRequestsEvent.Load)
    }

    fun onEvent(event: ProviderRequestsEvent) = when (event) {
        ProviderRequestsEvent.Load -> load()
        is ProviderRequestsEvent.Accept -> accept(event.request)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(ProviderRequestsState(requests = apiGetAllRequests()))
        }
    }

    private fun accept(req: ProviderRequest) {
        screenModelScope.launch {
            mutate { copy(acceptingId = req.userId) }
            if (apiAcceptRequest(req.userId)) {
                mutate { copy(requests = requests.filter { it.userId != req.userId }, acceptingId = null) }
                AppState.toast("Provider request accepted!")
            } else {
                mutate { copy(acceptingId = null) }
                AppState.toast("Failed to accept", true)
            }
        }
    }

    private fun mutate(block: ProviderRequestsState.() -> ProviderRequestsState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
