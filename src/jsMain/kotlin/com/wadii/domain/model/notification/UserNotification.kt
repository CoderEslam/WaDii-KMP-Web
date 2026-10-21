package com.wadii.domain.model.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One row of the in-app inbox (`GET /notifications/my`). */
@Serializable
data class UserNotification(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("body") val body: String = "",
    @SerialName("type") val type: String? = null,
    @SerialName("isRead") val isRead: Boolean = false,
    /** The order this is about (NEW_ORDER, NEW_RESPONSE, RESPONSE_*, ORDER_*), if any. */
    @SerialName("orderId") val orderId: Long? = null,
    @SerialName("createdAt") val createdAt: String = ""
)
