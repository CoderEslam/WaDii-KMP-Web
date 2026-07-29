package com.wadii.domain.model.call.livekit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveKitTokenRequest(
    @SerialName("roomName")
    val roomName: String = "",
    @SerialName("identity")
    val identity: String = "",
    @SerialName("participantName")
    val participantName: String = ""
)
