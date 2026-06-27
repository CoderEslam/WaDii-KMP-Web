package com.wadii.domain.model.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseCallback(
    @SerialName("comment")
    val comment: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("responsesState")
    val responsesState: String = "",
)
