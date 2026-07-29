package com.wadii.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkTime(
    @SerialName("closeTime")
    val closeTime: String = "",
    @SerialName("day")
    val day: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("startTime")
    val startTime: String = ""
)