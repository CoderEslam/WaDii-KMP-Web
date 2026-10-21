package com.wadii.pages.admin.provider

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
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
        .onStart { requests() }
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    override fun onEvent(event: ProviderRequestsEvent) {
        when (event) {
            is ProviderRequestsEvent.Accept -> accept(event.request)
            is ProviderRequestsEvent.Reject -> updateState { it.copy(rejectDraft = event.request, rejectReason = "") }
            is ProviderRequestsEvent.SetReason -> updateState { it.copy(rejectReason = event.value) }
            ProviderRequestsEvent.DismissReject -> updateState { it.copy(rejectDraft = null, rejectReason = "") }
            ProviderRequestsEvent.ConfirmReject -> _state.value.rejectDraft?.let { reject(it, _state.value.rejectReason) }
        }
    }

    private fun requests() = screenModelScope.launch {
        adminDashboardUseCase.requests { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = it.requests.isEmpty()) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            requests = data.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    /** Creates the provider, switches the user's role and removes the application; the applicant is notified. */
    private fun accept(req: ProviderRequestModel) = screenModelScope.launch {
        if (_state.value.acceptingId != 0L || _state.value.rejectingId != 0L) return@launch
        updateState { it.copy(acceptingId = req.id) }
        adminDashboardUseCase.acceptRequest(id = req.id) { response ->
            response.handelState(
                onSuccess = {
                    AppState.toast("${req.name} is now a seller")
                    updateState { it.copy(requests = it.requests.filter { r -> r.id != req.id }, acceptingId = 0L) }
                },
                onError = { error, _ ->
                    AppState.toast(error.ifBlank { "Couldn't accept the application" }, true)
                    updateState { it.copy(acceptingId = 0L) }
                    requests() // a 404/409 usually means someone already handled it
                }
            )
        }
    }

    private fun reject(req: ProviderRequestModel, reason: String) = screenModelScope.launch {
        if (_state.value.acceptingId != 0L || _state.value.rejectingId != 0L) return@launch
        updateState { it.copy(rejectingId = req.id) }
        adminDashboardUseCase.rejectRequest(id = req.id, reason = reason) { response ->
            response.handelState(
                onSuccess = {
                    AppState.toast("Application rejected")
                    updateState {
                        it.copy(
                            requests = it.requests.filter { r -> r.id != req.id },
                            rejectingId = 0L, rejectDraft = null, rejectReason = ""
                        )
                    }
                },
                onError = { error, _ ->
                    AppState.toast(error.ifBlank { "Couldn't reject the application" }, true)
                    updateState { it.copy(rejectingId = 0L) }
                }
            )
        }
    }
}
