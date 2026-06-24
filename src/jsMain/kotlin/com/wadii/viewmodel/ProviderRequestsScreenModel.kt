package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiAcceptRequest
import com.wadii.api.apiGetAllRequests
import com.wadii.model.ProviderRequest
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class ProviderRequestsEvent {
    object Load : ProviderRequestsEvent()
    data class Accept(val request: ProviderRequest) : ProviderRequestsEvent()
}

data class ProviderRequestsData(
    val requests: List<ProviderRequest>,
    val acceptingId: Long? = null
)

class ProviderRequestsScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<ProviderRequestsData>>(UiState.Loading)
        private set

    init { onEvent(ProviderRequestsEvent.Load) }

    fun onEvent(event: ProviderRequestsEvent) = when (event) {
        ProviderRequestsEvent.Load -> load()
        is ProviderRequestsEvent.Accept -> accept(event.request)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(ProviderRequestsData(apiGetAllRequests()))
        }
    }

    private fun accept(req: ProviderRequest) {
        screenModelScope.launch {
            mutate { copy(acceptingId = req.id) }
            if (apiAcceptRequest(req.id)) {
                mutate { copy(requests = requests.filter { it.id != req.id }, acceptingId = null) }
                AppState.toast("Provider request accepted!")
            } else {
                mutate { copy(acceptingId = null) }
                AppState.toast("Failed to accept", true)
            }
        }
    }

    private fun mutate(block: ProviderRequestsData.() -> ProviderRequestsData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
