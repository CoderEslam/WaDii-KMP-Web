package com.wadii.domain.model.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `POST /agora/token`. `uid` must equal the signed-in user's id; the channel is `<smallerId>_<largerId>`. */
@Serializable
data class AgoraTokenRequest(
    @SerialName("channelName") val channelName: String,
    @SerialName("uid") val uid: Int
)

@Serializable
data class AgoraTokenResponse(
    @SerialName("token") val token: String = "",
    @SerialName("appId") val appId: String = ""
)

/** `POST /providers/update-services` replaces the provider's whole service set. */
@Serializable
data class ProviderServicesRequest(
    @SerialName("providerId") val providerId: Long,
    @SerialName("serviceIds") val serviceIds: List<Long>
)
