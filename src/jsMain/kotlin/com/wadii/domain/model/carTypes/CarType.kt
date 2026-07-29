package com.wadii.domain.model.carTypes


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CarType(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = ""
)
