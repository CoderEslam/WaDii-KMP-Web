package com.wadii.screens.savedOffers

import com.wadii.domain.model.offers.saved.SavedOffer

data class SavedOffersState(
    val offers: List<SavedOffer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
