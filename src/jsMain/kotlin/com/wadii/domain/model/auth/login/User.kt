package com.wadii.domain.model.auth.login


import com.wadii.domain.model.city.City
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("backgroundImage")
    val backgroundImage: String? = null,
    @SerialName("city")
    val city: com.wadii.domain.model.city.City? = _root_ide_package_.com.wadii.domain.model.city.City(),
    @SerialName("email")
    val email: String = "",
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("following")
    val following: List<Following?> = listOf(),
    @SerialName("id")
    val id: Int = 0,
    @SerialName("image")
    val image: String? = null,
    @SerialName("lastName")
    val lastName: String = "",
    @SerialName("orders")
    val orders: List<Order?> = listOf(),
    @SerialName("password")
    val password: String = "",
    @SerialName("phone")
    val phone: String = "",
    @SerialName("provider")
    val provider: Provider? = Provider(),
    @SerialName("role")
    val role: String = "",
    @SerialName("token")
    val token: String = "",
    val fullName: String = "$firstName $lastName"
) {

    @Serializable
    data class Following(
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
    data class Order(
        @SerialName("carModelYear")
        val carModelYear: String = "",
        @SerialName("comment")
        val comment: String = "",
        @SerialName("date")
        val date: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("responses")
        val responses: List<Response> = listOf(),
        @SerialName("services")
        val services: List<Service> = listOf(),
        @SerialName("spareParts")
        val spareParts: List<SparePart> = listOf()
    ) {
        @Serializable
        data class Response(
            @SerialName("comment")
            val comment: String = "",
            @SerialName("id")
            val id: Int = 0,
            @SerialName("provider")
            val provider: Provider = Provider(),
            @SerialName("sparePartsPrices")
            val sparePartsPrices: List<SparePartsPrice> = listOf()
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
                val links: List<Link> = listOf(),
                @SerialName("offers")
                val offers: List<Offer> = listOf(),
                @SerialName("rate")
                val rate: Double = 0.0,
                @SerialName("user")
                val user: User = User()
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
                data class Link(
                    @SerialName("id")
                    val id: Int = 0,
                    @SerialName("link")
                    val link: String = ""
                )

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
                    val phone: String = "",
                    @SerialName("role")
                    val role: String = "",
                    @SerialName("token")
                    val token: String = ""
                )
            }

            @Serializable
            data class SparePartsPrice(
                @SerialName("id")
                val id: Int = 0,
                @SerialName("price")
                val price: Double = 0.0
            )
        }

        @Serializable
        data class Service(
            @SerialName("id")
            val id: Int = 0,
            @SerialName("name")
            val name: String = ""
        )

        @Serializable
        data class SparePart(
            @SerialName("id")
            val id: Int = 0,
            @SerialName("sparePartName")
            val sparePartName: String = ""
        )
    }

    @Serializable
    data class Provider(
        @SerialName("branches")
        val branches: List<Branche> = listOf(),
        @SerialName("followers")
        val followers: List<Follower> = listOf(),
        @SerialName("followersCount")
        val followersCount: Int = 0,
        @SerialName("id")
        val id: Int = 0,
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
        val services: List<Service> = listOf()
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
        data class Link(
            @SerialName("id")
            val id: Int = 0,
            @SerialName("link")
            val link: String = ""
        )

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
            val services: List<Service> = listOf(),
            @SerialName("title")
            val title: String = ""
        ) {
            @Serializable
            data class Service(
                @SerialName("id")
                val id: Int = 0,
                @SerialName("name")
                val name: String = ""
            )
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

        @Serializable
        data class Service(
            @SerialName("id")
            val id: Int = 0,
            @SerialName("name")
            val name: String = ""
        )
    }
}
