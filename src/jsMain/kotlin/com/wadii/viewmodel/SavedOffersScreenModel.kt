package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiGetSavedOffers
import com.wadii.api.apiRemoveSavedOffer
import com.wadii.model.Offer
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class SavedOffersEvent {
    object Load : SavedOffersEvent()
    data class Remove(val offer: Offer) : SavedOffersEvent()
}

data class SavedOffersData(val offers: List<Offer>)

class SavedOffersScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<SavedOffersData>>(UiState.Loading)
        private set

    init { onEvent(SavedOffersEvent.Load) }

    fun onEvent(event: SavedOffersEvent) = when (event) {
        SavedOffersEvent.Load -> load()
        is SavedOffersEvent.Remove -> remove(event.offer)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(SavedOffersData(apiGetSavedOffers()))
        }
    }

    private fun remove(offer: Offer) {
        screenModelScope.launch {
            if (apiRemoveSavedOffer(offer.id)) {
                val data = (state as? UiState.Success)?.data ?: return@launch
                state = UiState.Success(data.copy(offers = data.offers.filter { it.id != offer.id }))
                AppState.toast("Removed from saved")
            } else {
                AppState.toast("Failed to remove", true)
            }
        }
    }
}
