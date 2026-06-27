package com.wadii.domain.model.offers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedOfferRequest(
    @SerialName("offerId")
    val offerId: Int = 0
)
