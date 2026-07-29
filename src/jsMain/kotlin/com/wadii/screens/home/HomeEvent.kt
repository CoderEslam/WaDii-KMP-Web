package com.wadii.screens.home

import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.service.Service


sealed class HomeEvent {

    data class SelectService(val service: Service) : HomeEvent()

    data class ToggleSaveOffer(val offer: OfferResponse) : HomeEvent()
}
