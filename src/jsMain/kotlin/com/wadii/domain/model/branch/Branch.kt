package com.wadii.domain.model.branch

import com.wadii.domain.model.WorkTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Branche(
    @SerialName("address")
    val address: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("workTimes")
    val workTimes: List<com.wadii.domain.model.WorkTime> = listOf()
)