package com.wadii.domain.model.ads


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsertAds(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("advertiserName")
    val advertiserName: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("endDate")
    val endDate: String = "",
    @SerialName("imageUrl")
    val imageUrl: String = "",
    @SerialName("priority")
    val priority: Int = 0,
    @SerialName("startDate")
    val startDate: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("targetUrl")
    val targetUrl: String = "",
    @SerialName("title")
    val title: String = ""
)