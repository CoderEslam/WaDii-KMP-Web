package com.wadii.pages.shared.profile

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.api.readBytes
import com.wadii.domain.model.provider.ProviderRequest
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.state.AppState
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RequestProviderViewModel(
    private val providerUseCase: ProviderUseCase,
    private val servicesUseCase: ServicesUseCase
) : BaseViewModel<RequestProviderState, RequestProviderEvent>() {

    override val initialState: RequestProviderState get() = RequestProviderState()

    override val state: StateFlow<RequestProviderState> = _state
        .onStart { loadServices() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: RequestProviderEvent) {
        when (event) {
            RequestProviderEvent.Load -> loadServices()

            is RequestProviderEvent.SetName -> updateState { it.copy(name = event.value) }
            is RequestProviderEvent.SetAddress -> updateState { it.copy(address = event.value) }
            is RequestProviderEvent.SetPhoneNumber -> updateState { it.copy(phoneNumber = event.value) }

            is RequestProviderEvent.ToggleService -> updateState {
                val ids = it.selectedServiceIds
                it.copy(selectedServiceIds = if (event.serviceId in ids) ids - event.serviceId else ids + event.serviceId)
            }

            RequestProviderEvent.AddLink -> updateState { it.copy(links = it.links + "") }
            is RequestProviderEvent.RemoveLink -> updateState {
                it.copy(links = it.links.filterIndexed { i, _ -> i != event.linkIndex })
            }
            is RequestProviderEvent.SetLink -> updateState {
                it.copy(links = it.links.mapIndexed { i, l -> if (i == event.linkIndex) event.value else l })
            }

            is RequestProviderEvent.SetFrontIdImage -> updateState { it.copy(frontIdImage = event.file) }
            is RequestProviderEvent.SetBackIdImage -> updateState { it.copy(backIdImage = event.file) }
            is RequestProviderEvent.SetTaxCardFront -> updateState { it.copy(taxCardFront = event.file) }
            is RequestProviderEvent.SetTaxCardBack -> updateState { it.copy(taxCardBack = event.file) }

            RequestProviderEvent.Submit -> submit()
        }
    }

    private fun loadServices() {
        updateState { it.copy(loading = true) }
        screenModelScope.launch {
            servicesUseCase.getServiceList { response ->
                response.handelState(
                    onSuccess = { data -> updateState { it.copy(allServices = data.data ?: emptyList(), loading = false) } },
                    onError = { e, _ -> updateState { it.copy(error = e, loading = false) } }
                )
            }
        }
    }

    private fun submit() {
        val s = _state.value
        val userId = AppState.user?.id
        if (userId == null || s.name.isBlank() || s.address.isBlank() || s.phoneNumber.isBlank() || s.selectedServiceIds.isEmpty()) {
            AppState.toast("Please fill in all required fields", true)
            return
        }
        if (s.frontIdImage == null || s.backIdImage == null || s.taxCardFront == null || s.taxCardBack == null) {
            AppState.toast("Please upload all required documents", true)
            return
        }

        screenModelScope.launch {
            updateState { it.copy(submitting = true, error = null) }
            val request = ProviderRequest(
                name = s.name,
                userId = userId.toLong(),
                frontIdImage = s.frontIdImage.readBytes(),
                backIdImage = s.backIdImage.readBytes(),
                taxCardFront = s.taxCardFront.readBytes(),
                taxCardBack = s.taxCardBack.readBytes(),
                address = s.address,
                phoneNumber = s.phoneNumber,
                serviceIds = s.selectedServiceIds.toList(),
                links = s.links.filter { it.isNotBlank() }
            )
            providerUseCase.requestProvider(request) { r ->
                r.handelState(
                    onSuccess = {
                        updateState { it.copy(submitting = false, submittedSuccessfully = true) }
                        AppState.toast("Request submitted! We'll review it shortly.")
                    },
                    onError = { e, _ ->
                        updateState { it.copy(submitting = false, error = e) }
                        AppState.toast("Failed to submit request", true)
                    }
                )
            }
        }
    }
}
