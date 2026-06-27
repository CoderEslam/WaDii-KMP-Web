package com.wadii.domain.model.provider


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateServiceRequest(
    @SerialName("providerId")
    val providerId: Int = 0,
    @SerialName("serviceIds")
    val serviceIds: List<Int> = listOf()
)