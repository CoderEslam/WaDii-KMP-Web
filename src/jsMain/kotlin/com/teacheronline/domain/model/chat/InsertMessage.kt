package com.teacheronline.domain.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsertMessage(
    @SerialName("text")
    val text: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("toUserId")
    val toUserId: Long = 0
)
