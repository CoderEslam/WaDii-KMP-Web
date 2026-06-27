package com.wadii.screens.home

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiAllProviders
import com.wadii.data.api.apiFilterProvidersByService
import com.wadii.data.api.apiGetActiveAds
import com.wadii.data.api.apiGetAllOffers
import com.wadii.data.api.apiGetAllServices
import com.wadii.data.api.apiRemoveSavedOffer
import com.wadii.data.api.apiSaveOffer
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class HomeScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<HomeState>>(UiState.Loading)
        private set

    init {
        onEvent(HomeEvent.Load)
    }

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
            state = UiState.Success(HomeState(offers, ads, providers, providers, services))
        }
    }

    private fun selectService(id: Int?) {
        val d = (state as? UiState.Success)?.data ?: return
        if (id == null) {
            state = UiState.Success(d.copy(selectedServiceId = 0, filteredProviders = d.allProviders))
            return
        }
        state = UiState.Success(d.copy(selectedServiceId = id, filterLoading = true))
        screenModelScope.launch {
            val filtered = apiFilterProvidersByService(id)
            val cur = (state as? UiState.Success)?.data ?: return@launch
            state = UiState.Success(cur.copy(filteredProviders = filtered, filterLoading = false))
        }
    }

    private fun toggleSave(offer: OfferResponse) {
        screenModelScope.launch {
            if (offer.saved) apiRemoveSavedOffer(offer.id) else apiSaveOffer(offer.id)
            val updated = apiGetAllOffers().take(6)
            val cur = (state as? UiState.Success)?.data ?: return@launch
            state = UiState.Success(cur.copy(offers = updated))
            AppState.toast(if (offer.saved) "Offer removed" else "Offer saved!")
        }
    }
}
