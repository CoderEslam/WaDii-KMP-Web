package com.wadii.domain.model.call.livekit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveKitTokenResponse(
    @SerialName("url")
    val url: String = "",
    @SerialName("token")
    val token: String = ""
)
