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
    val fromUserId: Int = 0,
    @SerialName("toUserId")
    val toUserId: Int = 0,
    @SerialName("fromUserName")
    val fromUserName: String = "",
    @SerialName("fromUserImage")
    val fromUserImage: String? = null
)

@Serializable
data class CallSocketFrame(
    val event: String,
    val data: CallSignal
)
