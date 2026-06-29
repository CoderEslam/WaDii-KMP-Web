package com.wadii.domain.model.offers


import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.domain.model.offers.saved.SavedOffer.Offer
import com.wadii.domain.model.provider.ProviderModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OfferResponse(
    @SerialName("description")
    val description: String = "",
    @SerialName("endDate")
    val endDate: String = "",
    @SerialName("id")
    val id: Long = 0,
    @SerialName("provider")
    val provider: ProviderModel = ProviderModel(),
    @SerialName("title")
    val title: String = "",
    @SerialName("saved")
    val saved: Boolean = false
) {

    fun toSavedOffer() = SavedOffer(
        id = id,
        offer = SavedOffer.Offer(
            description = description,
            endDate = endDate,
            id = id,
            title = title,
            provider = provider
        ),
        user = provider.user,
    )

    fun toChatContent(): ChatContact = ChatContact(
        contact = provider.user,
        lastMessage = "",
        lastMessageAt = "",
        unreadCount = 0
    )
}

