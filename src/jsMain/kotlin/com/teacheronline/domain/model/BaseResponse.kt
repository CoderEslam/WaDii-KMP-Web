package com.teacheronline.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("data")
    val `data`: T? = null,
    @SerialName("message")
    val message: String = "",
    @SerialName("statusCode")
    val statusCode: Int = 0,
    @SerialName("timestamp")
    val timestamp: String = ""
)
