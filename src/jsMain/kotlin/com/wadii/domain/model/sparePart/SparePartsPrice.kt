package com.wadii.domain.model.sparePart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SparePartsPrice(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("price")
    val price: Double = 0.0,
    @SerialName("sparePart")
    val sparePart: com.wadii.domain.model.sparePart.SparePart = _root_ide_package_.com.wadii.domain.model.sparePart.SparePart()
)