package com.wadii.domain.model.province

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsertProvince(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("countryId")
    val countryId: Long = 0
)
