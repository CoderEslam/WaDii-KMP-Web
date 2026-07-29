package com.wadii.domain.model.ads


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ads(
    @SerialName("advertiserName")
    val advertiserName: String = "",
    @SerialName("clicks")
    val clicks: Int = 0,
    @SerialName("createdAt")
    val createdAt: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("endDate")
    val endDate: String = "",
    @SerialName("id")
    val id: Long = 0,
    @SerialName("imageUrl")
    val imageUrl: String = "",
    @SerialName("impressions")
    val impressions: Int = 0,
    @SerialName("priority")
    val priority: Int = 0,
    @SerialName("startDate")
    val startDate: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("targetUrl")
    val targetUrl: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("updatedAt")
    val updatedAt: String = ""
)