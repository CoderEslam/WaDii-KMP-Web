package com.wadii.domain.model.offers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewOfferRequest(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("title")
    val title: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("endDate")
    val endDate: String = "",
    @SerialName("serviceIds:")
    val serviceIds: Set<Long> = emptySet(),
    @SerialName("providerId")
    val providerId: Long = 0
) {
    val isNotEmpty: Boolean
        get() = title != null && !title.trim { it <= ' ' }
            .isEmpty() && description != null && !description.trim { it <= ' ' }
            .isEmpty() && endDate != null && providerId != null
}