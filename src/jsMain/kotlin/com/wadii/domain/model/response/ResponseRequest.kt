package com.wadii.domain.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseRequest(
    @SerialName("providerId") val providerId: Int = 0,
    @SerialName("orderId") val orderId: Int = 0,
    @SerialName("comment") val comment: String = "",
    @SerialName("sparePartsPrice") val sparePartsPrice: List<SparePartPrice> = emptyList(),
    @SerialName("latitude") val latitude: Double = 0.0,
    @SerialName("longitude") val longitude: Double = 0.0
) {
    @Serializable
    data class SparePartPrice(
        @SerialName("id") val id: Int = 0,
        @SerialName("price") val price: Double = 0.0
    )
}
