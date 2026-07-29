package com.wadii.domain.model.service


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsertService(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("name")
    val name: String = ""
)