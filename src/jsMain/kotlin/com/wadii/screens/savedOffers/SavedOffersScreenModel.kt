package com.wadii.screens.savedOffers

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.offers.SavedOfferRequest
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.domain.usecase.OfferUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedOffersViewModel(private val offerUseCase: OfferUseCase) : BaseViewModel<SavedOffersState, SavedOffersEvent>() {

    override val initialState: SavedOffersState
        get() = SavedOffersState()

    override val state: StateFlow<SavedOffersState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: SavedOffersEvent) {
        when (event) {
            SavedOffersEvent.Load -> load()
            is SavedOffersEvent.Remove -> remove(event.offer)
        }
    }

    private fun load() = screenModelScope.launch {
        offerUseCase.getMySavedOffers { response ->
            response.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(offers = data.data ?: emptyList(), isLoading = false) } },
                onError = { error, _ -> updateState { it.copy(error = error, isLoading = false) } }
            )
        }
    }

    private fun remove(offer: SavedOffer) = screenModelScope.launch {
        offerUseCase.removeSavedOffer(offer.id) { response ->
            response.handelState(
                onLoading = {},
                onSuccess = { _ ->
                    updateState { it.copy(offers = it.offers.filter { o -> o.id != offer.id }) }
                    AppState.toast("Removed from saved")
                },
                onError = { _, _ -> AppState.toast("Failed to remove", true) }
            )
        }
    }
}
