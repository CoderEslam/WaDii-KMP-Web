package com.wadii.domain.model.service

import com.wadii.domain.model.WorkTime
import com.wadii.domain.model.link.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Service(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("providers")
    val providers: List<Provider> = listOf()
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
        val links: List<com.wadii.domain.model.link.Link> = listOf(),
        @SerialName("offers")
        val offers: List<Offer> = listOf(),
        @SerialName("rate")
        val rate: Double = 0.0,
        @SerialName("user")
        val user: User = User(),
        @SerialName("workTimes")
        val workTimes: List<com.wadii.domain.model.WorkTime> = listOf()
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
            val phone: String? = null,
            @SerialName("role")
            val role: String = "",
            @SerialName("token")
            val token: String = ""
        )
    }
}