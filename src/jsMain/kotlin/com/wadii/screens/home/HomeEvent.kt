package com.wadii.screens.home

import com.wadii.domain.model.offers.OfferResponse


sealed class HomeEvent {

    data class SelectService(val id: Long) : HomeEvent()

    data class ToggleSaveOffer(val offer: OfferResponse) : HomeEvent()
    object ClearMessage : HomeEvent()
}
