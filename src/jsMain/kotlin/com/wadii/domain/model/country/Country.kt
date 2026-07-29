package com.wadii.domain.model.country

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("name")
    val name: String = ""
)
