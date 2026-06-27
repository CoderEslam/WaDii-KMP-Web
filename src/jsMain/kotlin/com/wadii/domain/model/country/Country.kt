package com.wadii.domain.model.country

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = ""
)
