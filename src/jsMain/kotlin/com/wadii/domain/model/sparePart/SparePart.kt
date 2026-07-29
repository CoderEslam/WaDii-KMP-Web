package com.wadii.domain.model.sparePart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SparePart(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("sparePartName")
    val sparePartName: String = ""
)