package com.wadii.domain.model.provider

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProviderByAdminRequest(
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("cityId")
    val cityId: Long,
    @SerialName("providerName")
    val providerName: String,
    @SerialName("rate")
    val rate: Double,
    @SerialName("serviceIds")
    val serviceIds: List<Int>,
    @SerialName("branches")
    val branches: List<CreateBranchRequest>,
    @SerialName("links")
    val links: List<CreateLinkRequest>,
    @SerialName("offers")
    val offers: List<CreateOfferRequest>
)

@Serializable
data class CreateBranchRequest(
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: String,
    @SerialName("workTimes")
    val workTimes: List<CreateWorkTimeRequest>
)

@Serializable
data class CreateWorkTimeRequest(
    @SerialName("day")
    val day: String,
    @SerialName("startTime")
    val startTime: String,
    @SerialName("closeTime")
    val closeTime: String
)

@Serializable
data class CreateLinkRequest(
    @SerialName("link")
    val link: String
)

@Serializable
data class CreateOfferRequest(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("endDate")
    val endDate: String
)
