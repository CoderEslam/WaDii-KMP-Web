package com.wadii.domain.model.order


import com.wadii.domain.model.city.City
import com.wadii.domain.model.provider.ProviderModel
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
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("status")
    val status: String = "",
    /** The provider the customer picked directly, or null when the order was matched by services. */
    @SerialName("providerId")
    val providerId: Long? = null,
    @SerialName("acceptedProviderId")
    val acceptedProviderId: Long? = null,
    @SerialName("acceptedResponseId")
    val acceptedResponseId: Long? = null,
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
        RESPONSES_AVAILABLE,
        ACCEPTED,
        IN_PROGRESS,
        COMPLETED,
        CANCELED
    }

    @Serializable
    data class OrderResponse(
        @SerialName("comment")
        val comment: String = "",
        @SerialName("id")
        val id: Int = 0,
        /** PENDING | ACCEPTED | DECLINED | CANCELED */
        @SerialName("responsesState")
        val state: String = "",
        @SerialName("totalPrice")
        val totalPrice: Double = 0.0,
        @SerialName("latitude")
        val latitude: Double? = null,
        @SerialName("longitude")
        val longitude: Double? = null,
        @SerialName("provider")
        val provider: ProviderModel = ProviderModel(),
        @SerialName("sparePartsPrices")
        val sparePartsPrices: List<SparePartsPrice> = listOf()
    )

    /** The customer of this order as a chat contact (chat exists only once an order is accepted). */
    fun customerChatContact(): com.wadii.domain.model.chat.ChatContact =
        com.wadii.domain.model.chat.ChatContact(
            contact = com.wadii.domain.model.auth.login.User(
                id = user.id.toLong(),
                firstName = user.firstName,
                lastName = user.lastName,
                image = user.image,
                phone = user.phone,
                email = user.email,
                role = user.role
            )
        )

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
        val city: City? = City(),
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
