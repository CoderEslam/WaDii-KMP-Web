package com.wadii.domain.model.offers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewOfferRequest(
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("endDate") val endDate: String = ""
)
