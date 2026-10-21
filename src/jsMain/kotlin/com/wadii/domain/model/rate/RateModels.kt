package com.wadii.domain.model.rate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `POST /rate/insert` needs the `orderId` of a COMPLETED order the caller owns (one rating per order); the
 * provider and the author come from the server. `POST /rate/update` needs the rate `id`.
 */
@Serializable
data class RateRequest(
    @SerialName("id") val id: Long? = null,
    @SerialName("orderId") val orderId: Long? = null,
    @SerialName("comment") val comment: String = "",
    @SerialName("rate") val rate: Double
)

@Serializable
data class RateModel(
    @SerialName("id") val id: Long = 0,
    @SerialName("comment") val comment: String = "",
    @SerialName("rate") val rate: Double = 0.0
)
