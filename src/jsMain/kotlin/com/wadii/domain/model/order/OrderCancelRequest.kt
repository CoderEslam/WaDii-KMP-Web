package com.wadii.domain.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderCancelRequest(
    @SerialName("orderId")
    val orderId: Int = 0,
    @SerialName("reasonId")
    val reasonId: Int = 0
)
