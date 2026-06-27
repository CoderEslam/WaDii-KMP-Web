package com.wadii.domain.model.order


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    @SerialName("carModelYear")
    val carModelYear: String = "",
    @SerialName("comment")
    val comment: String = "",
    @SerialName("date")
    val date: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("latitude")
    val latitude: Double = 0.0,
    @SerialName("longitude")
    val longitude: Double = 0.0,
    @SerialName("servicesIds")
    val servicesIds: List<Int> = listOf(),
    @SerialName("spareParts")
    val spareParts: List<SparePart> = listOf(),
    @SerialName("userId")
    val userId: Int = 0
) {
    @Serializable
    data class SparePart(
        @SerialName("sparePartName")
        val sparePartName: String = ""
    )
}