package com.wadii.pages.admin.provider

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.providerRequests.ProviderRequestModel
import com.wadii.domain.usecase.AdminDashboardUseCase
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
            is ProviderRequestsEvent.Accept -> {
                accept(event.request)
            }
            is ProviderRequestsEvent.Reject -> {
                reject(event.request)
            }
        }
    }

    private fun requests() = screenModelScope.launch {
        adminDashboardUseCase.requests { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            requests = data.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun accept(req: ProviderRequestModel) = screenModelScope.launch {
        adminDashboardUseCase.acceptRequest(id = req.id) { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            requests = it.requests.filter { r -> r.id != req.id },
                            acceptingId = 0L
                        )
                    }
                }, onError = { error, code ->
                    updateState {
                        it.copy(
                            error = error,
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    private fun reject(req: ProviderRequestModel) = screenModelScope.launch {
        adminDashboardUseCase.rejectRequest(id = req.id) { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(rejectingId = req.id) }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            requests = it.requests.filter { r -> r.id != req.id },
                            rejectingId = 0L
                        )
                    }
                }, onError = { error, code ->
                    updateState {
                        it.copy(
                            error = error,
                            rejectingId = 0L
                        )
                    }
                }
            )
        }
    }
}
