package com.wadii.domain.model.order


import com.wadii.domain.model.auth.login.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderCallbackResponse(
    @SerialName("carModelYear")
    val carModelYear: String = "",
    @SerialName("comment")
    val comment: String = "",
    @SerialName("date")
    val date: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("latitude")
    val latitude: Double? = 0.0,
    @SerialName("longitude")
    val longitude: Double? = 0.0,
    @SerialName("services")
    val services: List<Service> = listOf(),
    @SerialName("spareParts")
    val spareParts: List<com.wadii.domain.model.auth.login.User.Order.SparePart> = listOf(),
    @SerialName("user")
    val user: com.wadii.domain.model.auth.login.User = _root_ide_package_.com.wadii.domain.model.auth.login.User()
) {
    @Serializable
    data class Service(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("name")
        val name: String = ""
    )
}
