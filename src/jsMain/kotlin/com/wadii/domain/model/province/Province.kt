package com.wadii.domain.model.province

import com.wadii.domain.model.country.Country
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Province(
    @SerialName("country")
    val country: Country = Country(),
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = ""
) {
}