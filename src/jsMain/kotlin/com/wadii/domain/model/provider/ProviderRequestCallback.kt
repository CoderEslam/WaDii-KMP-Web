package com.wadii.domain.model.provider


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProviderRequestCallback(
    @SerialName("address")
    val address: String = "",
    @SerialName("backIdImage")
    val backIdImage: String = "",
    @SerialName("frontIdImage")
    val frontIdImage: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("links")
    val links: List<String>? = emptyList(),
    @SerialName("name")
    val name: String = "",
    @SerialName("phoneNumber")
    val phoneNumber: String = "",
    @SerialName("requestedAt")
    val requestedAt: String = "",
    @SerialName("services")
    val services: List<Service> = listOf(),
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
    ) {
        @Serializable
        data class City(
            @SerialName("id")
            val id: Int = 0,
            @SerialName("name")
            val name: String = "",
            @SerialName("provinceId")
            val provinceId: ProvinceId = ProvinceId()
        ) {
            @Serializable
            data class ProvinceId(
                @SerialName("countryId")
                val countryId: CountryId = CountryId(),
                @SerialName("id")
                val id: Int = 0,
                @SerialName("name")
                val name: String = ""
            ) {
                @Serializable
                data class CountryId(
                    @SerialName("id")
                    val id: Int = 0,
                    @SerialName("name")
                    val name: String = ""
                )
            }
        }
    }
}
