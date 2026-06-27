package com.wadii.pages.provider.offers

import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.service.Service

data class ProviderOffersState(
    val offers: List<OfferResponse> = emptyList(),
    val services: List<Service> = emptyList(),
    val showModal: Boolean = false,
    val editOffer: OfferResponse? = null,
    val saving: Boolean = false
)
