package com.wadii.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T : Any>(
    @SerialName("data")
    val `data`: T,
    @SerialName("message")
    val message: String = "",
    @SerialName("statusCode")
    val statusCode: Int = 0,
    @SerialName("timestamp")
    val timestamp: String = ""
)
