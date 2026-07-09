package com.wadii.domain.model.call

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CallSignal(
    @SerialName("channelName")
    val channelName: String = "",
    @SerialName("callType")
    val callType: String = "VIDEO",
    @SerialName("fromUserId")
    val fromUserId: Long = 0,
    @SerialName("toUserId")
    val toUserId: Long = 0,
    @SerialName("fromUserName")
    val fromUserName: String = "",
    @SerialName("fromUserImage")
    val fromUserImage: String? = null,
    @SerialName("provider")
    val provider: String? = "LIVEKIT"
)

@Serializable
data class CallSocketFrame(
    val event: String,
    val data: CallSignal
)
