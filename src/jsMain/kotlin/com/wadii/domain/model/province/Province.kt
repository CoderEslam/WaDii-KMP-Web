package com.wadii.domain.model.province

import com.wadii.domain.model.country.Country
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Province(
    @SerialName("countryId")
    val country: com.wadii.domain.model.country.Country = _root_ide_package_.com.wadii.domain.model.country.Country(),
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = ""
) {
}