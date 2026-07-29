package com.wadii.domain.model.follow


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Followers(
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
        @SerialName("branches")
        val branches: List<Branche> = listOf(),
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
        data class Branche(
            @SerialName("address")
            val address: String = "",
            @SerialName("id")
            val id: Int = 0,
            @SerialName("name")
            val name: String = "",
            @SerialName("workTimes")
            val workTimes: List<WorkTime> = listOf()
        ) {
            @Serializable
            data class WorkTime(
                @SerialName("closeTime")
                val closeTime: String = "",
                @SerialName("day")
                val day: String = "",
                @SerialName("id")
                val id: Int = 0,
                @SerialName("startTime")
                val startTime: String = ""
            )
        }

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
