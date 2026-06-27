package com.wadii.domain.model.serach

import com.wadii.domain.model.branch.Branche
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.service.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchModel(
    @SerialName("branches")
    val branches: List<Branche> = listOf(),
@SerialName("offers")
val offers: List<OfferResponse> = listOf(),

@SerialName("providers")
val providers: List<ProviderModel> = listOf(),

@SerialName("services")
val services: List<Service> = listOf()
)