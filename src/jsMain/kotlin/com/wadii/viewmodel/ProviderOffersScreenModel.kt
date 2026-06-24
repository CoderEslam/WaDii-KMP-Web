package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.*
import com.wadii.model.Offer
import com.wadii.model.Service
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class ProviderOffersEvent {
    object Load : ProviderOffersEvent()
    data class ShowModal(val offer: Offer?) : ProviderOffersEvent()
    object CloseModal : ProviderOffersEvent()
    data class Delete(val offerId: Long) : ProviderOffersEvent()
    data class Save(val offer: Offer?, val title: String, val description: String, val endDate: String, val selectedServices: Set<Long>) : ProviderOffersEvent()
}

data class ProviderOffersData(
    val offers: List<Offer> = emptyList(),
    val services: List<Service> = emptyList(),
    val showModal: Boolean = false,
    val editOffer: Offer? = null,
    val saving: Boolean = false
)

class ProviderOffersScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<ProviderOffersData>>(UiState.Loading)
        private set

    init { onEvent(ProviderOffersEvent.Load) }

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
            state = UiState.Success(ProviderOffersData(offers, services))
        }
    }

    private fun delete(id: Long) {
        screenModelScope.launch {
            if (apiDeleteOffer(id)) { AppState.toast("Deleted"); load() }
            else AppState.toast("Failed to delete", true)
        }
    }

    private fun save(offer: Offer?, title: String, description: String, endDate: String, services: Set<Long>) {
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

    private fun mutate(block: ProviderOffersData.() -> ProviderOffersData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
