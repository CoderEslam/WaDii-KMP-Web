package com.wadii.screens.savedOffers

import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.offers.saved.SavedOffer


sealed class SavedOffersEvent {
    object Load : SavedOffersEvent()
    data class Remove(val offer: SavedOffer) : SavedOffersEvent()
}
