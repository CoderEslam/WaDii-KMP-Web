package com.wadii.domain.model.chat


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageCallbackWebSocket(
    @SerialName("data")
    val `data`: Data = Data(),
    @SerialName("event")
    val event: String = ""
) {
    @Serializable
    data class Data(
        @SerialName("createdAt")
        val createdAt: String = "",
        @SerialName("fromUser")
        val fromUser: FromUser = FromUser(),
        @SerialName("id")
        val id: Int = 0,
        @SerialName("isRead")
        val isRead: Boolean = false,
        @SerialName("text")
        val text: String = "",
        @SerialName("toUser")
        val toUser: ToUser = ToUser(),
        @SerialName("type")
        val type: String = ""
    ) {
        @Serializable
        data class FromUser(
            @SerialName("backgroundImage")
            val backgroundImage: String = "",
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
            val image: String = "",
            @SerialName("lastName")
            val lastName: String = "",
            @SerialName("phone")
            val phone: String = "",
            @SerialName("role")
            val role: String = ""
        ) {
            @Serializable
            data class City(
                @SerialName("id")
                val id: Int = 0,
                @SerialName("name")
                val name: String = "",
                @SerialName("province")
                val province: Province = Province()
            ) {
                @Serializable
                data class Province(
                    @SerialName("country")
                    val country: Country = Country(),
                    @SerialName("id")
                    val id: Int = 0,
                    @SerialName("name")
                    val name: String = ""
                ) {
                    @Serializable
                    data class Country(
                        @SerialName("id")
                        val id: Int = 0,
                        @SerialName("name")
                        val name: String = ""
                    )
                }
            }
        }

        @Serializable
        data class ToUser(
            @SerialName("backgroundImage")
            val backgroundImage: String? = "",
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
            val image: String? = "",
            @SerialName("lastName")
            val lastName: String = "",
            @SerialName("phone")
            val phone: String = "",
            @SerialName("role")
            val role: String = ""
        ) {
            @Serializable
            data class City(
                @SerialName("id")
                val id: Int = 0,
                @SerialName("name")
                val name: String = "",
                @SerialName("province")
                val province: Province = Province()
            ) {
                @Serializable
                data class Province(
                    @SerialName("country")
                    val country: Country = Country(),
                    @SerialName("id")
                    val id: Int = 0,
                    @SerialName("name")
                    val name: String = ""
                ) {
                    @Serializable
                    data class Country(
                        @SerialName("id")
                        val id: Int = 0,
                        @SerialName("name")
                        val name: String = ""
                    )
                }
            }
        }
    }
}