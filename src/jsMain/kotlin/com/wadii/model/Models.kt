package com.wadii.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val message: String = "",
    val statusCode: Int = 0,
    val timestamp: String? = null
)

@Serializable
data class User(
    val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val token: String? = null,
    val fcmToken: String? = null,
    val image: String? = null,
    val backgroundImage: String? = null,
    val phone: String? = null,
    val role: String = "USER",
    val provider: Provider? = null,
    val city: City? = null
) {
    val fullName get() = "$firstName $lastName".trim()
    val initial get() = firstName.firstOrNull()?.toString() ?: "?"
}

@Serializable
data class Provider(
    val id: Long = 0,
    val name: String = "",
    val rate: Double = 0.0,
    val followersCount: Long = 0,
    val services: List<Service>? = null,
    val branches: List<Branch>? = null,
    val links: List<Link>? = null
)

@Serializable
data class Service(
    val id: Long = 0,
    val name: String = ""
)

@Serializable
data class Offer(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val endDate: String = "",
    val provider: Provider? = null,
    val services: List<Service>? = null,
    val saved: Boolean = false
)

@Serializable
data class Order(
    val id: Long = 0,
    val carModelYear: String = "",
    val comment: String = "",
    val date: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val user: User? = null,
    val services: List<Service>? = null,
    val spareParts: List<SparePart>? = null,
    val responses: List<OrderResponse>? = null
)

@Serializable
data class OrderResponse(
    val id: Long = 0,
    val comment: String = "",
    val state: String = "PENDING",
    val provider: Provider? = null,
    val sparePartsPrice: List<SparePartPrice>? = null
)

@Serializable
data class SparePart(
    val id: Long = 0,
    val sparePartName: String = ""
)

@Serializable
data class SparePartPrice(
    val id: Long = 0,
    val price: Double = 0.0,
    val spareParts: SparePart? = null
)

@Serializable
data class Message(
    val id: Long = 0,
    val text: String = "",
    val type: String = "TEXT",
    val createdAt: String = "",
    val isRead: Boolean = false,
    val fromUser: User? = null,
    val toUser: User? = null
)

@Serializable
data class ChatContact(
    val id: Long = 0,
    val contact: User? = null,
    val lastMessage: String? = null,
    val lastMessageAt: String? = null,
    val messageType: String? = null
)

@Serializable
data class UserNotification(
    val id: Long = 0,
    val title: String = "",
    val body: String = "",
    val type: String = "",
    val isRead: Boolean = false,
    val createdAt: String = ""
)

@Serializable
data class Advertisement(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val targetUrl: String? = null,
    val advertiserName: String = "",
    val status: String = "ACTIVE",
    val priority: Int = 1,
    val impressions: Long = 0,
    val clicks: Long = 0,
    val startDate: String = "",
    val endDate: String = ""
)

@Serializable
data class Branch(
    val id: Long = 0,
    val name: String = "",
    val address: String = "",
    val workTimes: List<WorkTime>? = null
)

@Serializable
data class WorkTime(
    val id: Long = 0,
    val day: String = "",
    val startTime: String = "",
    val closeTime: String = ""
)

@Serializable
data class Link(
    val id: Long = 0,
    val link: String = ""
)

@Serializable
data class Country(val id: Long = 0, val name: String = "")

@Serializable
data class Province(val id: Long = 0, val name: String = "", val country: Country? = null)

@Serializable
data class City(val id: Long = 0, val name: String = "", val province: Province? = null)

@Serializable
data class ProviderRequest(
    val id: Long = 0,
    val name: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val frontIdImage: String? = null,
    val backIdImage: String? = null,
    val links: String? = null,
    val user: User? = null,
    val services: List<Service>? = null
)

@Serializable
data class SearchResult(
    val offers: List<Offer> = emptyList(),
    val services: List<Service> = emptyList(),
    val providers: List<Provider> = emptyList(),
    val branches: List<Branch> = emptyList()
)

// --- Request bodies ---

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val cityId: Long = 0,
    val fcmToken: String = "",
    val userType: Int = 0,
    val providerName: String = ""
)

@Serializable
data class IdRef(val id: Long)

@Serializable
data class MessageRequest(
    val toUser: IdRef,
    val text: String,
    val type: String = "TEXT"
)

@Serializable
data class SaveOfferRequest(val offerId: Long)
