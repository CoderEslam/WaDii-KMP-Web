package com.wadii.screens.home

import com.wadii.domain.model.offers.OfferResponse


sealed class HomeEvent {
    object Load : HomeEvent()
    data class SelectService(val id: Int?) : HomeEvent()
    data class ToggleSaveOffer(val offer: OfferResponse) : HomeEvent()
}
