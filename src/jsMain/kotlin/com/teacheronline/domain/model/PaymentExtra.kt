package com.teacheronline.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentExtra(
    val id: Long = 0,
    val dateTime: String = "",
    val price: Double = 0.0,
    val notes: String? = null
)

@Serializable
data class PaymentExtraDto(
    val price: Double = 0.0,
    val notes: String? = null
)
