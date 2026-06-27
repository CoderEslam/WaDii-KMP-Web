package com.wadii.domain.model.providerRequests


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.wadii.domain.model.city.City


@Serializable
data class ProviderRequestModel(
    @SerialName("address")
    val address: String = "",
    @SerialName("backIdImage")
    val backIdImage: String? = null,
    @SerialName("frontIdImage")
    val frontIdImage: String? = null,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("links")
    val links: List<String> = listOf(),
    @SerialName("name")
    val name: String = "",
    @SerialName("phoneNumber")
    val phoneNumber: String = "",
    @SerialName("requestedAt")
    val requestedAt: String = "",
    @SerialName("services")
    val services: List<Service> = listOf(),
    @SerialName("taxCardBack")
    val taxCardBack: String? = null,
    @SerialName("taxCardFront")
    val taxCardFront: String? = null,
    @SerialName("user")
    val user: User = User()
) {
    @Serializable
    data class Service(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("name")
        val name: String = ""
    )

    @Serializable
    data class User(
        @SerialName("backgroundImage")
        val backgroundImage: String? = null,
        @SerialName("city")
        val city: City = City(),
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
