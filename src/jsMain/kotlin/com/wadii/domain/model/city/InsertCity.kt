package com.wadii.domain.model.city

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsertCity(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("provinceId")
    val provinceId: Long = 0
)
