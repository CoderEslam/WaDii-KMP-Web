package com.wadii.domain.model.shop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---- provider shop details: branches, work times, links ----

@Serializable
data class BranchRequest(
    @SerialName("id") val id: Long? = null,
    @SerialName("name") val name: String,
    @SerialName("address") val address: String,
    @SerialName("providerId") val providerId: Long
)

@Serializable
data class WorkTimeRequest(
    @SerialName("id") val id: Long? = null,
    @SerialName("branchId") val branchId: Long,
    @SerialName("startTime") val startTime: String,
    @SerialName("closeTime") val closeTime: String,
    @SerialName("day") val day: String
)

@Serializable
data class LinkRequest(
    @SerialName("id") val id: Long? = null,
    @SerialName("providerId") val providerId: Long,
    @SerialName("link") val link: String
)

// ---- admin catalog: car types and common spare parts ----

@Serializable
data class NameRequest(
    @SerialName("id") val id: Long? = null,
    @SerialName("name") val name: String
)

@Serializable
data class CommonSparePart(
    @SerialName("id") val id: Long = 0,
    @SerialName("name") val name: String = ""
)


/** Body of `POST /providers/availability`: the provider pauses or resumes new orders. */
@Serializable
data class AvailabilityRequest(
    @SerialName("acceptingOrders") val acceptingOrders: Boolean
)
