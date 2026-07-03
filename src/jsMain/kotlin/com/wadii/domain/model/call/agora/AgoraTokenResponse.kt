package com.wadii.domain.model.call.agora


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AgoraTokenResponse(
    @SerialName("appId")
    val appId: String = "",
    @SerialName("token")
    val token: String = ""
)
