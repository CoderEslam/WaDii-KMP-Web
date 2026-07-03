package com.wadii.domain.model.call.agora


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgoraTokenRequest(
    @SerialName("channelName")
    val channelName: String = "",
    @SerialName("uid")
    val uid: Int = 0
)