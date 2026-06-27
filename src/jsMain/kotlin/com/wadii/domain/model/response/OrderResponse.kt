package com.wadii.domain.model.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    @SerialName("comment")
    val comment: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("provider")
    val provider: Provider = Provider(),
    @SerialName("sparePartsPrices")
    val sparePartsPrices: List<SparePartsPrice> = listOf()
) {
    @Serializable
    data class Provider(
        @SerialName("followers")
        val followers: List<Follower> = listOf(),
        @SerialName("followersCount")
        val followersCount: Int = 0,
        @SerialName("id")
        val id: Int = 0,
        @SerialName("links")
        val links: List<Link> = listOf(),
        @SerialName("offers")
        val offers: List<Offer> = listOf(),
        @SerialName("rate")
        val rate: Double = 0.0,
        @SerialName("user")
        val user: User = User()
    ) {
        @Serializable
        data class Follower(
            @SerialName("id")
            val id: Id = Id()
        ) {
            @Serializable
            data class Id(
                @SerialName("providerId")
                val providerId: Int = 0,
                @SerialName("userId")
                val userId: Int = 0
            )
        }

        @Serializable
        data class Link(
            @SerialName("id")
            val id: Int = 0,
            @SerialName("link")
            val link: String = ""
        )

        @Serializable
        data class Offer(
            @SerialName("description")
            val description: String = "",
            @SerialName("endDate")
            val endDate: String = "",
            @SerialName("id")
            val id: Int = 0,
            @SerialName("title")
            val title: String = ""
        )

        @Serializable
        data class User(
            @SerialName("email")
            val email: String = "",
            @SerialName("fcmToken")
            val fcmToken: String = "",
            @SerialName("firstName")
            val firstName: String = "",
            @SerialName("id")
            val id: Int = 0,
            @SerialName("image")
            val image: String? = null,
            @SerialName("lastName")
            val lastName: String = "",
            @SerialName("password")
            val password: String = "",
            @SerialName("phone")
            val phone: String = "",
            @SerialName("role")
            val role: String = "",
            @SerialName("token")
            val token: String = ""
        )
    }

    @Serializable
    data class SparePartsPrice(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("price")
        val price: Double = 0.0,
        @SerialName("sparePart")
        val sparePart: SparePart = SparePart()
    ){
        @Serializable
        data class SparePart(
            @SerialName("id")
            val id: Int = 0,
            @SerialName("sparePartName")
            val sparePartName: String = ""
        )
    }
}
