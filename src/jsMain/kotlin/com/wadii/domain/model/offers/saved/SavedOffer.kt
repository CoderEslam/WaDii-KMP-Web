package com.wadii.domain.model.offers.saved


import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.provider.ProviderModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SavedOffer(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("offer")
    val offer: Offer = Offer(),
    @SerialName("user")
    val user: User = User()
) {
    @Serializable
    data class Offer(
        @SerialName("description")
        val description: String = "",
        @SerialName("endDate")
        val endDate: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("title")
        val title: String = "",
        @SerialName("provider")
        val provider: ProviderModel = ProviderModel(),
    )
}
