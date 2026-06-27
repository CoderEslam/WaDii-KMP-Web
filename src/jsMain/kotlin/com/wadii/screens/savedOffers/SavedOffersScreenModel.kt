package com.wadii.screens.savedOffers

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiGetSavedOffers
import com.wadii.data.api.apiRemoveSavedOffer
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class SavedOffersScreenModel : ScreenModel {
    var state by mutableStateOf<UiState<SavedOffersState>>(UiState.Loading)
        private set

    init { onEvent(SavedOffersEvent.Load) }

    fun onEvent(event: SavedOffersEvent) = when (event) {
        SavedOffersEvent.Load -> load()
        is SavedOffersEvent.Remove -> remove(event.offer)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(SavedOffersState(offers = apiGetSavedOffers()))
        }
    }

    private fun remove(offer: SavedOffer) {
        screenModelScope.launch {
            if (apiRemoveSavedOffer(offer.id)) {
                mutate { copy(offers = offers.filter { it.id != offer.id }) }
                AppState.toast("Removed from saved")
            } else {
                AppState.toast("Failed to remove", true)
            }
        }
    }

    private fun mutate(block: SavedOffersState.() -> SavedOffersState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
