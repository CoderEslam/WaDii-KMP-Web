package com.wadii.pages.provider.offers

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.api.apiGetAllOffers
import com.wadii.data.api.apiInsertOffer
import com.wadii.data.api.apiUpdateOffer
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.usecase.OfferUseCase
import com.wadii.state.AppState
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProviderOffersViewModel(
    private val offerUseCase: OfferUseCase,
    private val servicesUseCase: ServicesUseCase
) : BaseViewModel<ProviderOffersState, ProviderOffersEvent>() {

    override val initialState: ProviderOffersState get() = ProviderOffersState()

    override val state: StateFlow<ProviderOffersState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ProviderOffersEvent) {
        when (event) {
            ProviderOffersEvent.Load -> load()
            is ProviderOffersEvent.ShowModal -> updateState { it.copy(showModal = true, editOffer = event.offer) }
            ProviderOffersEvent.CloseModal -> updateState { it.copy(showModal = false, editOffer = null) }
            is ProviderOffersEvent.Delete -> delete(event.offerId)
            is ProviderOffersEvent.Save -> save(event.offer, event.title, event.description, event.endDate, event.selectedServices)
        }
    }

    private fun load() = screenModelScope.launch {
        updateState { it.copy(isLoading = true) }
        val offers = apiGetAllOffers()
        updateState { it.copy(offers = offers, isLoading = false) }
        servicesUseCase.getServiceList { r ->
            r.handelState(
                onLoading = {},
                onSuccess = { data -> updateState { it.copy(services = data.data ?: emptyList()) } },
                onError = { _, _ -> }
            )
        }
    }

    private fun delete(id: Long) = screenModelScope.launch {
        offerUseCase.deleteOffer(id.toInt()) { r ->
            r.handelState(
                onLoading = {},
                onSuccess = { _ -> AppState.toast("Deleted"); load() },
                onError = { _, _ -> AppState.toast("Failed to delete", true) }
            )
        }
    }

    private fun save(offer: OfferResponse?, title: String, description: String, endDate: String, services: Set<Long>) {
        updateState { it.copy(saving = true) }
        screenModelScope.launch {
            val body = buildMap<String, Any?> {
                put("title", title); put("description", description); put("endDate", endDate)
                put("services", services.map { mapOf("id" to it) })
                offer?.let { put("id", it.id) }
            }
            val result = if (offer != null) apiUpdateOffer(body) else apiInsertOffer(body)
            updateState { it.copy(saving = false) }
            if (result != null) {
                AppState.toast(if (offer != null) "Offer updated!" else "Offer created!")
                load()
            } else {
                AppState.toast("Failed to save offer", true)
            }
        }
    }
}
