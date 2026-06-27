package com.wadii.pages.provider.offers

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiDeleteOffer
import com.wadii.data.api.apiGetAllOffers
import com.wadii.data.api.apiGetAllServices
import com.wadii.data.api.apiInsertOffer
import com.wadii.data.api.apiUpdateOffer
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class ProviderOffersScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<ProviderOffersState>>(UiState.Loading)
        private set

    init {
        onEvent(ProviderOffersEvent.Load)
    }

    fun onEvent(event: ProviderOffersEvent) = when (event) {
        ProviderOffersEvent.Load -> load()
        is ProviderOffersEvent.ShowModal -> mutate { copy(showModal = true, editOffer = event.offer) }
        ProviderOffersEvent.CloseModal -> mutate { copy(showModal = false, editOffer = null) }
        is ProviderOffersEvent.Delete -> delete(event.offerId)
        is ProviderOffersEvent.Save -> save(event.offer, event.title, event.description, event.endDate, event.selectedServices)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            val offers = apiGetAllOffers()
            val services = apiGetAllServices()
            state = UiState.Success(ProviderOffersState(offers, services))
        }
    }

    private fun delete(id: Long) {
        screenModelScope.launch {
            if (apiDeleteOffer(id)) { AppState.toast("Deleted"); load() }
            else AppState.toast("Failed to delete", true)
        }
    }

    private fun save(offer: OfferResponse?, title: String, description: String, endDate: String, services: Set<Int>) {
        mutate { copy(saving = true) }
        screenModelScope.launch {
            val body = buildMap<String, Any?> {
                put("title", title); put("description", description); put("endDate", endDate)
                put("services", services.map { mapOf("id" to it) })
                offer?.let { put("id", it.id) }
            }
            val result = if (offer != null) apiUpdateOffer(body) else apiInsertOffer(body)
            mutate { copy(saving = false) }
            if (result != null) {
                AppState.toast(if (offer != null) "Offer updated!" else "Offer created!")
                load()
            } else {
                AppState.toast("Failed to save offer", true)
            }
        }
    }

    private fun mutate(block: ProviderOffersState.() -> ProviderOffersState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
