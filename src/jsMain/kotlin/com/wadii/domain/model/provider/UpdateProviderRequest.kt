package com.wadii.domain.model.provider

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UpdateProviderRequest(
    @SerialName("id")
    val id: Long,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("email")
    val email: String,
    @SerialName("serviceIds")
    val serviceIds: List<Int>,
    @SerialName("branches")
    val branches: List<com.wadii.domain.model.provider.BranchRequest>,
    @SerialName("links")
    val links: List<com.wadii.domain.model.provider.EditableLink>,
    @SerialName("offers")
    val offers: List<com.wadii.domain.model.provider.OfferRequest>
)

@Serializable
data class BranchRequest(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: String,
    @SerialName("workTimes")
    val workTimes: List<com.wadii.domain.model.provider.WorkTimeRequest>
)

data class EditableBranch(
    val id: Int,
    val name: String,
    val address: String,
    val workTimes: List<com.wadii.domain.model.provider.EditableWorkTime>
)

@Serializable
data class WorkTimeRequest(
    @SerialName("id")
    val id: Int,
    @SerialName("day")
    val day: String,
    @SerialName("startTime")
    val startTime: String,
    @SerialName("closeTime")
    val closeTime: String
)

@Serializable
data class OfferRequest(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("endDate")
    val endDate: String
)


@Serializable
data class EditableWorkTime(
    @SerialName("id")
    val id: Int,
    @SerialName("day")
    val day: String,
    @SerialName("startTime")
    val startTime: String,
    @SerialName("closeTime")
    val closeTime: String
)

@Serializable
data class EditableLink(
    @SerialName("id")
    val id: Int,
    @SerialName("link")
    var link: String
)
@Serializable
data class EditableOffer(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("endDate")
    val endDate: String
)