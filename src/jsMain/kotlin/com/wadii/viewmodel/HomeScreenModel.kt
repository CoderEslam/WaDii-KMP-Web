package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.*
import com.wadii.model.Advertisement
import com.wadii.model.Offer
import com.wadii.model.Provider
import com.wadii.model.Service
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class HomeEvent {
    object Load : HomeEvent()
    data class SelectService(val id: Long?) : HomeEvent()
    data class ToggleSaveOffer(val offer: Offer) : HomeEvent()
}

data class HomeData(
    val offers: List<Offer> = emptyList(),
    val ads: List<Advertisement> = emptyList(),
    val allProviders: List<Provider> = emptyList(),
    val filteredProviders: List<Provider> = emptyList(),
    val services: List<Service> = emptyList(),
    val selectedServiceId: Long? = null,
    val filterLoading: Boolean = false
) {
    val visibleOffers: List<Offer> get() = if (selectedServiceId == null) offers
        else offers.filter { o -> o.services?.any { it.id == selectedServiceId } == true }
}

class HomeScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<HomeData>>(UiState.Loading)
        private set

    init { onEvent(HomeEvent.Load) }

    fun onEvent(event: HomeEvent) = when (event) {
        HomeEvent.Load -> load()
        is HomeEvent.SelectService -> selectService(event.id)
        is HomeEvent.ToggleSaveOffer -> toggleSave(event.offer)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            val offers = apiGetAllOffers().take(6)
            val ads = apiGetActiveAds().take(3)
            val providers = apiAllProviders() ?: emptyList()
            val services = apiGetAllServices()
            state = UiState.Success(HomeData(offers, ads, providers, providers, services))
        }
    }

    private fun selectService(id: Long?) {
        val d = (state as? UiState.Success)?.data ?: return
        if (id == null) { state = UiState.Success(d.copy(selectedServiceId = null, filteredProviders = d.allProviders)); return }
        state = UiState.Success(d.copy(selectedServiceId = id, filterLoading = true))
        screenModelScope.launch {
            val filtered = apiFilterProvidersByService(id)
            val cur = (state as? UiState.Success)?.data ?: return@launch
            state = UiState.Success(cur.copy(filteredProviders = filtered, filterLoading = false))
        }
    }

    private fun toggleSave(offer: Offer) {
        screenModelScope.launch {
            if (offer.saved) apiRemoveSavedOffer(offer.id) else apiSaveOffer(offer.id)
            val updated = apiGetAllOffers().take(6)
            val cur = (state as? UiState.Success)?.data ?: return@launch
            state = UiState.Success(cur.copy(offers = updated))
            AppState.toast(if (offer.saved) "Offer removed" else "Offer saved!")
        }
    }
}
