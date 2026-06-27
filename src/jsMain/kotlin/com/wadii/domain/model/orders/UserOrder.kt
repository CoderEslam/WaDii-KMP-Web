package com.wadii.domain.model.orders


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserOrder(
    @SerialName("comment")
    val comment: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("order")
    val order: Order = Order(),
    @SerialName("provider")
    val provider: Provider = Provider()
) {
    @Serializable
    data class Order(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("sparepart")
        val sparepart: List<Sparepart> = listOf(),
        @SerialName("user")
        val user: User = User()
    ) {
        @Serializable
        data class Sparepart(
            @SerialName("id")
            val id: Int = 0,
            @SerialName("name")
            val name: String = "",
            @SerialName("sparePartsPrice")
            val sparePartsPrice: SparePartsPrice = SparePartsPrice()
        ) {
            @Serializable
            data class SparePartsPrice(
                @SerialName("id")
                val id: Int = 0,
                @SerialName("price")
                val price: Double = 0.0
            )
        }

        @Serializable
        data class User(
            @SerialName("id")
            val id: Int = 0
        )
    }

    @Serializable
    data class Provider(
        @SerialName("email")
        val email: String = "",
        @SerialName("fcm")
        val fcm: String = "",
        @SerialName("firstName")
        val firstName: String = "",
        @SerialName("image")
        val image: String = "",
        @SerialName("lastName")
        val lastName: String = "",
        @SerialName("phone")
        val phone: String = "",
        @SerialName("providerId")
        val providerId: Int = 0,
        @SerialName("userId")
        val userId: Int = 0
    )
}
