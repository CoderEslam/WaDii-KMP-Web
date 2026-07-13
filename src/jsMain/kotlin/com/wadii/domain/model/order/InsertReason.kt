package com.wadii.domain.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsertReason(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("reason")
    val reason: String = ""
)
