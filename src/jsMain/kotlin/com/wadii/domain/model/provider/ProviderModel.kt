package com.wadii.domain.model.provider

import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.branch.Branche
import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.city.City
import com.wadii.domain.model.link.Link
import com.wadii.domain.model.service.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProviderModel(
    @SerialName("branches")
    val branches: List<Branche> = listOf(),
    @SerialName("followers")
    val followers: List<Follower> = listOf(),
    @SerialName("followersCount")
    val followersCount: Int = 0,
    @SerialName("id")
    val id: Long = 0,
    @SerialName("links")
    val links: List<Link> = listOf(),
    @SerialName("name")
    val name: String = "",
    @SerialName("offers")
    val offers: List<Offer> = listOf(),
    @SerialName("rate")
    val rate: Double = 0.0,
    @SerialName("rates")
    val rates: List<Rate> = listOf(),
    @SerialName("services")
    val services: List<Service> = listOf(),
    @SerialName("user")
    val user: User = User()
) {

    @Serializable
    data class Follower(
        @SerialName("id")
        val id: Id = Id(),
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
        data class User(
            @SerialName("backgroundImage")
            val backgroundImage: String? = null,
            @SerialName("city")
            val city: com.wadii.domain.model.city.City = _root_ide_package_.com.wadii.domain.model.city.City(),
            @SerialName("email")
            val email: String = "",
            @SerialName("fcmToken")
            val fcmToken: String? = "",
            @SerialName("firstName")
            val firstName: String = "",
            @SerialName("id")
            val id: Long = 0,
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
        }
    }

    @Serializable
    data class Offer(
        @SerialName("description")
        val description: String = "",
        @SerialName("endDate")
        val endDate: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("saved")
        val saved: Boolean = false,
        @SerialName("services")
        val services: List<com.wadii.domain.model.service.Service> = listOf(),
        @SerialName("title")
        val title: String = ""
    ) {
    }

    @Serializable
    data class Rate(
        @SerialName("comment")
        val comment: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("rate")
        val rate: Double = 0.0
    )


//    fun toUserChatContact(): ChatContact.User = ChatContact.User(
//        id = id,
//        firstName = firstName,
//        lastName = lastName,
//        image = image,
//        phone = phone,
//        email = email,
//        role = role,
//        fcmToken = fcmToken,
//    )
//

    fun toChatContent(): com.wadii.domain.model.chat.ChatContact =
        _root_ide_package_.com.wadii.domain.model.chat.ChatContact(
            contact = user,
            lastMessage = "",
            lastMessageAt = "",
            unreadCount = 0
        )

    fun toLoginUser() = _root_ide_package_.com.wadii.domain.model.auth.login.User(
        id = user.id,
        firstName = user.firstName,
        lastName = user.lastName,
        image = user.image,
        phone = user.phone,
        email = user.email,
        role = user.role,
        token = user.token,
        city = user.city,
        password = user.password,
        provider = _root_ide_package_.com.wadii.domain.model.auth.login.User.Provider(
            id = id,
        )
    )
}