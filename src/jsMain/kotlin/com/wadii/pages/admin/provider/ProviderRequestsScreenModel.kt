package com.wadii.pages.admin.provider

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.api.apiAcceptRequest
import com.wadii.domain.model.providerRequests.ProviderRequestModel
import com.wadii.domain.usecase.AdminDashboardUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProviderRequestsViewModel(
    private val adminDashboardUseCase: AdminDashboardUseCase
) : BaseViewModel<ProviderRequestsState, ProviderRequestsEvent>() {

    override val initialState: ProviderRequestsState get() = ProviderRequestsState()

    override val state: StateFlow<ProviderRequestsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ProviderRequestsEvent) {
        when (event) {
            ProviderRequestsEvent.Load -> load()
            is ProviderRequestsEvent.Accept -> accept(event.request)
        }
    }

    private fun load() = screenModelScope.launch {
        adminDashboardUseCase.requests { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(requests = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun accept(req: ProviderRequestModel) {
        updateState { it.copy(acceptingId = req.user.id.toLong()) }
        screenModelScope.launch {
            if (apiAcceptRequest(req.user.id)) {
                updateState { it.copy(requests = it.requests.filter { r -> r.id != req.id }, acceptingId = null) }
                AppState.toast("Provider request accepted!")
            } else {
                updateState { it.copy(acceptingId = null) }
                AppState.toast("Failed to accept", true)
            }
        }
    }
}
