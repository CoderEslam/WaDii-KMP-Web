package com.wadii.domain.model.chat

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SocketResponse(
    val event: String,
    val data: JsonElement
) {
    override fun toString(): String {
        return "SocketResponse(event='$event', data=$data)"
    }

}