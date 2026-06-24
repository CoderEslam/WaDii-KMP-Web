package com.wadii.api

import com.wadii.model.*
import com.wadii.state.AppState
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

// ── Auth ──────────────────────────────────────────────────────────────────────

suspend fun apiLogin(email: String, password: String): User? = runCatching {
    val r: ApiResponse<User> = httpClient.post("$BASE_URL/auth/login") {
        contentType(ContentType.Application.Json)
        setBody(AuthRequest(email = email, password = password))
    }.body()
    r.data
}.getOrNull()

suspend fun apiRegister(req: AuthRequest): User? = runCatching {
    val r: ApiResponse<User> = httpClient.post("$BASE_URL/auth/register") {
        contentType(ContentType.Application.Json)
        setBody(req)
    }.body()
    r.data
}.getOrNull()

// ── Users ─────────────────────────────────────────────────────────────────────

suspend fun apiGetMe(): User? = runCatching {
    val r: ApiResponse<User> = httpClient.get("$BASE_URL/users/me") { auth() }.body()
    r.data
}.getOrNull()

suspend fun apiIsOnline(userId: Long): Boolean = runCatching {
    httpClient.get("$BASE_URL/users/$userId/online") { auth() }.body<Boolean>()
}.getOrDefault(false)

// ── Countries / Provinces / Cities ───────────────────────────────────────────

suspend fun apiGetCountries(): List<Country> = runCatching {
    httpClient.get("$BASE_URL/countries").body<ApiResponse<List<Country>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiGetProvinces(countryId: Long): List<Province> = runCatching {
    httpClient.get("$BASE_URL/provinces/by-country/$countryId").body<ApiResponse<List<Province>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiGetCities(provinceId: Long): List<City> = runCatching {
    httpClient.get("$BASE_URL/cities/by-province/$provinceId").body<ApiResponse<List<City>>>().data ?: emptyList()
}.getOrDefault(emptyList())

// ── Providers ─────────────────────────────────────────────────────────────────

suspend fun apiGetMyProvider(): Provider? = runCatching {
    httpClient.get("$BASE_URL/providers/me") { auth() }.body<ApiResponse<Provider>>().data
}.getOrNull()

suspend fun apiGetProvider(id: Long): Provider? = runCatching {
    httpClient.get("$BASE_URL/providers/$id") { auth() }.body<ApiResponse<Provider>>().data
}.getOrNull()

suspend fun apiFilterProvidersByService(serviceId: Long): List<Provider> = runCatching {
    httpClient.get("$BASE_URL/providers/filter-by-service/$serviceId") { auth() }
        .body<ApiResponse<List<Provider>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiFollowProvider(id: Long): Boolean = runCatching {
    httpClient.post("$BASE_URL/providers/follow-provider/$id") { auth() }.status.isSuccess()
}.getOrDefault(false)

suspend fun apiUnfollowProvider(id: Long): Boolean = runCatching {
    httpClient.delete("$BASE_URL/providers/unfollow-provider/$id") { auth() }.status.isSuccess()
}.getOrDefault(false)

// ── Offers ────────────────────────────────────────────────────────────────────

suspend fun apiGetAllOffers(): List<Offer> = runCatching {
    httpClient.get("$BASE_URL/offers/readAll") { auth() }.body<ApiResponse<List<Offer>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiInsertOffer(body: Map<String, Any?>): Offer? = runCatching {
    httpClient.post("$BASE_URL/offers/insert") {
        auth(); contentType(ContentType.Application.Json); setBody(body)
    }.body<ApiResponse<Offer>>().data
}.getOrNull()

suspend fun apiUpdateOffer(body: Map<String, Any?>): Offer? = runCatching {
    httpClient.put("$BASE_URL/offers/update") {
        auth(); contentType(ContentType.Application.Json); setBody(body)
    }.body<ApiResponse<Offer>>().data
}.getOrNull()

suspend fun apiDeleteOffer(id: Long): Boolean = runCatching {
    httpClient.delete("$BASE_URL/offers/$id") { auth() }.status.isSuccess()
}.getOrDefault(false)

// ── Saved Offers ──────────────────────────────────────────────────────────────

suspend fun apiGetSavedOffers(): List<Offer> = runCatching {
    httpClient.get("$BASE_URL/saved-offers/my-saved-offers") { auth() }
        .body<ApiResponse<List<Offer>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiSaveOffer(offerId: Long): Boolean = runCatching {
    httpClient.post("$BASE_URL/saved-offers/insert") {
        auth(); contentType(ContentType.Application.Json)
        setBody(SaveOfferRequest(offer = IdRef(offerId)))
    }.status.isSuccess()
}.getOrDefault(false)

suspend fun apiRemoveSavedOffer(offerId: Long): Boolean = runCatching {
    httpClient.delete("$BASE_URL/saved-offers/remove/$offerId") { auth() }.status.isSuccess()
}.getOrDefault(false)

// ── Orders ────────────────────────────────────────────────────────────────────

suspend fun apiGetUserOrders(): List<Order> = runCatching {
    httpClient.get("$BASE_URL/orders/show-all-order-of-user") { auth() }
        .body<ApiResponse<List<Order>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiGetProviderOrders(): List<Order> = runCatching {
    httpClient.get("$BASE_URL/orders/show-all-order-of-provider") { auth() }
        .body<ApiResponse<List<Order>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiGetOrder(id: Long): Order? = runCatching {
    httpClient.get("$BASE_URL/orders/$id") { auth() }.body<ApiResponse<Order>>().data
}.getOrNull()

suspend fun apiInsertOrder(body: Map<String, Any?>): Order? = runCatching {
    httpClient.post("$BASE_URL/orders/insert") {
        auth(); contentType(ContentType.Application.Json); setBody(body)
    }.body<ApiResponse<Order>>().data
}.getOrNull()

suspend fun apiDeleteOrder(id: Long): Boolean = runCatching {
    httpClient.delete("$BASE_URL/orders/$id") { auth() }.status.isSuccess()
}.getOrDefault(false)

// ── Responses ─────────────────────────────────────────────────────────────────

suspend fun apiInsertResponse(body: Map<String, Any?>): OrderResponse? = runCatching {
    httpClient.post("$BASE_URL/responses/insert") {
        auth(); contentType(ContentType.Application.Json); setBody(body)
    }.body<ApiResponse<OrderResponse>>().data
}.getOrNull()

suspend fun apiAcceptResponse(id: Long): Boolean = runCatching {
    httpClient.post("$BASE_URL/responses/accept-response") {
        auth(); contentType(ContentType.Application.Json); setBody(mapOf("id" to id))
    }.status.isSuccess()
}.getOrDefault(false)

suspend fun apiCancelResponse(id: Long): Boolean = runCatching {
    httpClient.post("$BASE_URL/responses/cancel-response") {
        auth(); contentType(ContentType.Application.Json); setBody(mapOf("id" to id))
    }.status.isSuccess()
}.getOrDefault(false)

// ── Messages ──────────────────────────────────────────────────────────────────

suspend fun apiGetChatList(): List<ChatContact> = runCatching {
    httpClient.get("$BASE_URL/messages/chat-list") { auth() }
        .body<ApiResponse<List<ChatContact>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiGetConversation(userId: Long): List<Message> = runCatching {
    httpClient.get("$BASE_URL/messages/conversation/$userId") { auth() }
        .body<ApiResponse<List<Message>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiSendMessage(toUserId: Long, text: String): Message? = runCatching {
    httpClient.post("$BASE_URL/messages/insert") {
        auth(); contentType(ContentType.Application.Json)
        setBody(MessageRequest(toUser = IdRef(toUserId), text = text))
    }.body<ApiResponse<Message>>().data
}.getOrNull()

// ── Notifications ─────────────────────────────────────────────────────────────

suspend fun apiGetNotifications(): List<UserNotification> = runCatching {
    httpClient.get("$BASE_URL/notifications/my") { auth() }
        .body<ApiResponse<List<UserNotification>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiMarkRead(id: Long): Boolean = runCatching {
    httpClient.post("$BASE_URL/notifications/mark-read/$id") { auth() }.status.isSuccess()
}.getOrDefault(false)

suspend fun apiMarkAllRead(): Boolean = runCatching {
    httpClient.post("$BASE_URL/notifications/mark-all-read") { auth() }.status.isSuccess()
}.getOrDefault(false)

suspend fun apiDeleteNotification(id: Long): Boolean = runCatching {
    httpClient.delete("$BASE_URL/notifications/$id") { auth() }.status.isSuccess()
}.getOrDefault(false)

// ── Advertisements ────────────────────────────────────────────────────────────

suspend fun apiGetAllAds(): List<Advertisement> = runCatching {
    httpClient.get("$BASE_URL/advertisements/readAll") { auth() }
        .body<ApiResponse<List<Advertisement>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiGetActiveAds(): List<Advertisement> = runCatching {
    httpClient.get("$BASE_URL/advertisements/active") { auth() }
        .body<ApiResponse<List<Advertisement>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiInsertAd(body: Map<String, Any?>): Advertisement? = runCatching {
    httpClient.post("$BASE_URL/advertisements/insert") {
        auth(); contentType(ContentType.Application.Json); setBody(body)
    }.body<ApiResponse<Advertisement>>().data
}.getOrNull()

suspend fun apiUpdateAd(body: Map<String, Any?>): Advertisement? = runCatching {
    httpClient.put("$BASE_URL/advertisements/update") {
        auth(); contentType(ContentType.Application.Json); setBody(body)
    }.body<ApiResponse<Advertisement>>().data
}.getOrNull()

suspend fun apiDeleteAd(id: Long): Boolean = runCatching {
    httpClient.delete("$BASE_URL/advertisements/$id") { auth() }.status.isSuccess()
}.getOrDefault(false)

suspend fun apiTrackClick(id: Long) = runCatching {
    httpClient.post("$BASE_URL/advertisements/track-click/$id") { auth() }
}.getOrNull()

// ── Services ──────────────────────────────────────────────────────────────────

suspend fun apiGetAllServices(): List<Service> = runCatching {
    httpClient.get("$BASE_URL/services/readAll") { auth() }
        .body<ApiResponse<List<Service>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiInsertService(name: String): Service? = runCatching {
    httpClient.post("$BASE_URL/services/insert") {
        auth(); contentType(ContentType.Application.Json); setBody(mapOf("name" to name))
    }.body<ApiResponse<Service>>().data
}.getOrNull()

suspend fun apiUpdateService(id: Long, name: String): Service? = runCatching {
    httpClient.put("$BASE_URL/services/update") {
        auth(); contentType(ContentType.Application.Json); setBody(mapOf("id" to id, "name" to name))
    }.body<ApiResponse<Service>>().data
}.getOrNull()

suspend fun apiDeleteService(id: Long): Boolean = runCatching {
    httpClient.delete("$BASE_URL/services/$id") { auth() }.status.isSuccess()
}.getOrDefault(false)

// ── Search ────────────────────────────────────────────────────────────────────

suspend fun apiSearch(q: String): SearchResult = runCatching {
    httpClient.get("$BASE_URL/search") { auth(); parameter("q", q) }
        .body<ApiResponse<SearchResult>>().data ?: SearchResult()
}.getOrDefault(SearchResult())

// ── Provider Requests ─────────────────────────────────────────────────────────

suspend fun apiGetAllRequests(): List<ProviderRequest> = runCatching {
    httpClient.get("$BASE_URL/provider-requests/show-all") { auth() }
        .body<ApiResponse<List<ProviderRequest>>>().data ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiAcceptRequest(id: Long): Boolean = runCatching {
    httpClient.post("$BASE_URL/provider-requests/accept/$id") { auth() }.status.isSuccess()
}.getOrDefault(false)

// ── Image upload ──────────────────────────────────────────────────────────────

suspend fun apiUploadImage(file: org.w3c.files.File): String? {
    val token = AppState.token ?: return null
    return runCatching {
        val formData = org.w3c.xhr.FormData()
        formData.append("image", file, file.name)
        kotlin.coroutines.suspendCoroutine<String?> { cont ->
            val xhr = org.w3c.xhr.XMLHttpRequest()
            xhr.open("POST", "$BASE_URL/users/upload-image")
            xhr.setRequestHeader("Authorization", "Bearer $token")
            xhr.onload = {
                if (xhr.status.toInt() in 200..299) {
                    try {
                        val parsed: dynamic = JSON.parse(xhr.responseText)
                        val data: String? = parsed.data?.toString()
                        cont.resumeWith(Result.success(data))
                    } catch (_: Exception) { cont.resumeWith(Result.success(null)) }
                } else cont.resumeWith(Result.success(null))
                null
            }
            xhr.onerror = { cont.resumeWith(Result.success(null)); null }
            xhr.send(formData)
        }
    }.getOrNull()
}
