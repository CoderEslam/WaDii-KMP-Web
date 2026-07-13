package com.wadii.domain.model.order


import com.wadii.domain.model.sparePart.SparePart
import com.wadii.domain.model.sparePart.SparePartsPrice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderModel(
    @SerialName("carModelYear")
    val carModelYear: String = "",
    @SerialName("comment")
    val comment: String = "",
    @SerialName("date")
    val date: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("status")
    val status: String = "",
    @SerialName("responses")
    val responses: List<OrderResponse> = listOf(),
    @SerialName("services")
    val services: List<Service> = listOf(),
    @SerialName("spareParts")
    val spareParts: List<SparePart> = listOf(),
    @SerialName("user")
    val user: User = User()
) {


    enum class OrderStatus {
        PENDING,
        CANCELED
    }

    @Serializable
    data class OrderResponse(
        @SerialName("comment")
        val comment: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("state")
        val state: String = "",
        @SerialName("provider")
        val provider: Provider = Provider(),
        @SerialName("sparePartsPrices")
        val sparePartsPrices: List<SparePartsPrice> = listOf()
    ) {
        @Serializable
        data class Provider(
            @SerialName("followersCount")
            val followersCount: Int = 0,
            @SerialName("id")
            val id: Int = 0,
            @SerialName("name")
            val name: String = "",
            @SerialName("rate")
            val rate: Double = 0.0,
            @SerialName("user")
            val user: User = User()
        ) {
            @Serializable
            data class User(
                @SerialName("city")
                val city: com.wadii.domain.model.city.City? = _root_ide_package_.com.wadii.domain.model.city.City(),
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
    }

    @Serializable
    data class Service(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("name")
        val name: String = ""
    )


    @Serializable
    data class User(
        @SerialName("city")
        val city: com.wadii.domain.model.city.City? = _root_ide_package_.com.wadii.domain.model.city.City(),
        @SerialName("email")
        val email: String = "",
        @SerialName("fcmToken")
        val fcmToken: String = "",
        @SerialName("firstName")
        val firstName: String = "",
//        @SerialName("following")
//        val following: List<Any?> = listOf(),
        @SerialName("id")
        val id: Int = 0,
        @SerialName("image")
        val image: String? = "",
        @SerialName("lastName")
        val lastName: String = "",
        @SerialName("password")
        val password: String = "",
        @SerialName("phone")
        val phone: String = "",
//        @SerialName("provider")
//        val provider: Any? = null,
        @SerialName("role")
        val role: String = "",
        @SerialName("token")
        val token: String = ""
    ) {
    }
}
