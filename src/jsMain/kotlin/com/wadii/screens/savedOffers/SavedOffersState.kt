package com.wadii.screens.savedOffers

import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.offers.saved.SavedOffer


data class SavedOffersState(
    val offers: List<SavedOffer> = emptyList()
)
