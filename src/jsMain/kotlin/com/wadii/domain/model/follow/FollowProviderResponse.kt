package com.wadii.domain.model.follow


import com.wadii.domain.model.city.City
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FollowProviderResponse(
    @SerialName("id")
    val id: Id = Id(),
    @SerialName("provider")
    val provider: Provider = Provider(),
    @SerialName("user")
    val user: User = User()
) {
    @Serializable
    data class Id(
        @SerialName("providerId")
        val providerId: Int = 0,
        @SerialName("userId")
        val userId: Int = 0
    )

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
    }

    @Serializable
    data class User(
        @SerialName("city")
        val city: com.wadii.domain.model.city.City = _root_ide_package_.com.wadii.domain.model.city.City(),
        @SerialName("email")
        val email: String = "",
        @SerialName("fcmToken")
        val fcmToken: String? = "",
        @SerialName("firstName")
        val firstName: String = "",
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
        @SerialName("role")
        val role: String = "",
        @SerialName("token")
        val token: String = ""
    ) {
    }
}
