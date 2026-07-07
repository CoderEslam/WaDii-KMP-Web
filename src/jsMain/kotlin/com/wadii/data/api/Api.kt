package com.wadii.data.api

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.auth.login.LoginRequest
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.auth.register.RegisterRequest
import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.chat.InsertMessage
import com.wadii.domain.model.chat.InsertResponse
import com.wadii.domain.model.chat.ShowAllMessagesResponse
import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.offers.SavedOfferRequest
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.domain.model.order.OrderModel
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.provider.ProviderRequest
import com.wadii.domain.model.province.Province
import com.wadii.domain.model.response.OrderResponse
import com.wadii.domain.model.serach.SearchModel
import com.wadii.domain.model.service.Service
import com.wadii.state.AppState
import com.wadii.utils.Constants.BASE_URL
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.w3c.files.File
import org.w3c.xhr.FormData
import org.w3c.xhr.XMLHttpRequest
import kotlin.coroutines.suspendCoroutine

// ── Auth ──────────────────────────────────────────────────────────────────────

val httpClient = createHttpClient()
//suspend fun apiLogin(email: String, password: String): User? = runCatching {
//    val r: BaseResponse<User> = httpClient.post("$BASE_URL/auth/login") {
//        contentType(ContentType.Application.Json)
//        setBody(LoginRequest(email = email, password = password, fcmToken = "", userType = 0))
//    }.body()
//    r.data
//
//}.getOrNull()

//suspend fun apiRegister(req: RegisterRequest): User? = runCatching {
//    val r: BaseResponse<User> = httpClient.post("$BASE_URL/auth/register") {
//        contentType(ContentType.Application.Json)
//        setBody(req)
//    }.body()
//    r.data
//}.getOrNull()

// ── Users ─────────────────────────────────────────────────────────────────────

//suspend fun apiGetMe(): User? = runCatching {
//    val r: BaseResponse<User> = httpClient.get("$BASE_URL/users/me") { auth() }.body()
//    r.data
//}.getOrNull()
//
//suspend fun apiIsOnline(userId: Long): Boolean = runCatching {
//    httpClient.get("$BASE_URL/users/$userId/online") { auth() }.body<Boolean>()
//}.getOrDefault(false)

// ── Countries / Provinces / Cities ───────────────────────────────────────────

//suspend fun apiGetCountries(): List<Country> = runCatching {
//    httpClient.get("$BASE_URL/countries").body<BaseResponse<List<Country>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiGetProvinces(countryId: Int): List<Province> = runCatching {
//    httpClient.get("$BASE_URL/provinces/by-country/$countryId")
//        .body<BaseResponse<List<Province>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiGetCities(provinceId: Int): List<City> = runCatching {
//    httpClient.get("$BASE_URL/cities/by-province/$provinceId").body<BaseResponse<List<City>>>().data
//        ?: emptyList()
//}.getOrDefault(emptyList())

// ── Providers ─────────────────────────────────────────────────────────────────

//suspend fun apiAllProviders(): List<ProviderModel>? = runCatching {
//    httpClient.get("$BASE_URL/providers/show-all") { auth() }
//        .body<BaseResponse<List<ProviderModel>>>().data
//}.getOrNull()
//
//suspend fun apiGetMyProvider(): ProviderModel? = runCatching {
//    httpClient.get("$BASE_URL/providers/me") { auth() }.body<BaseResponse<ProviderModel>>().data
//}.getOrNull()
//
//suspend fun apiGetProvider(id: Int): ProviderModel? = runCatching {
//    httpClient.get("$BASE_URL/providers/show/$id") { auth() }.body<BaseResponse<ProviderModel>>().data
//}.getOrNull()
//
//suspend fun apiFilterProvidersByService(serviceId: Int): List<ProviderModel> = runCatching {
//    httpClient.get("$BASE_URL/providers/filter-by-service/$serviceId") { auth() }
//        .body<BaseResponse<List<ProviderModel>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiFollowProvider(id: Int): Boolean = runCatching {
//    httpClient.post("$BASE_URL/providers/follow-provider/$id") { auth() }.status.isSuccess()
//}.getOrDefault(false)
//
//suspend fun apiUnfollowProvider(id: Int): Boolean = runCatching {
//    httpClient.delete("$BASE_URL/providers/unfollow-provider/$id") { auth() }.status.isSuccess()
//}.getOrDefault(false)

// ── Offers ────────────────────────────────────────────────────────────────────

suspend fun apiGetAllOffers(): List<OfferResponse> = runCatching {
    httpClient.get("$BASE_URL/offers/show-all") { auth() }.body<BaseResponse<List<OfferResponse>>>().data
        ?: emptyList()
}.getOrDefault(emptyList())

suspend fun apiInsertOffer(body: Map<String, Any?>): OfferResponse? = runCatching {
    httpClient.post("$BASE_URL/offers/insert") {
        auth(); contentType(ContentType.Application.Json); setBody(body)
    }.body<BaseResponse<OfferResponse>>().data
}.getOrNull()

suspend fun apiUpdateOffer(body: Map<String, Any?>): OfferResponse? = runCatching {
    httpClient.put("$BASE_URL/offers/update") {
        auth(); contentType(ContentType.Application.Json); setBody(body)
    }.body<BaseResponse<OfferResponse>>().data
}.getOrNull()

//suspend fun apiDeleteOffer(id: Long): Boolean = runCatching {
//    httpClient.delete("$BASE_URL/offers/$id") { auth() }.status.isSuccess()
//}.getOrDefault(false)

// ── Saved Offers ──────────────────────────────────────────────────────────────

//suspend fun apiGetSavedOffers(): List<SavedOffer> = runCatching {
//    httpClient.get("$BASE_URL/saved-offers/my-saved-offers") { auth() }
//        .body<BaseResponse<List<SavedOffer>>>().data ?: emptyList()
//}.getOrDefault(emptyList())

//suspend fun apiSaveOffer(offerId: Long): Boolean = runCatching {
//    httpClient.post("$BASE_URL/saved-offers/insert") {
//        auth(); contentType(ContentType.Application.Json)
//        setBody(SavedOfferRequest(offerId = offerId))
//    }.status.isSuccess()
//}.getOrDefault(false)
//
//suspend fun apiRemoveSavedOffer(offerId: Long): Boolean = runCatching {
//    httpClient.delete("$BASE_URL/saved-offers/remove/$offerId") { auth() }.status.isSuccess()
//}.getOrDefault(false)

// ── Orders ────────────────────────────────────────────────────────────────────

//suspend fun apiGetUserOrders(): List<OrderModel> = runCatching {
//    httpClient.get("$BASE_URL/orders/show-all-order-of-user") { auth() }
//        .body<BaseResponse<List<OrderModel>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiGetProviderOrders(): List<OrderModel> = runCatching {
//    httpClient.get("$BASE_URL/orders/show-all-order-of-provider") { auth() }
//        .body<BaseResponse<List<OrderModel>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiGetOrder(id: Int): OrderModel? = runCatching {
//    httpClient.get("$BASE_URL/orders/$id") { auth() }.body<BaseResponse<OrderModel>>().data
//}.getOrNull()

//suspend fun apiInsertOrder(body: Map<String, Any?>): OrderModel? = runCatching {
//    httpClient.post("$BASE_URL/orders/insert") {
//        auth(); contentType(ContentType.Application.Json); setBody(body)
//    }.body<BaseResponse<OrderModel>>().data
//}.getOrNull()
//
//suspend fun apiDeleteOrder(id: Long): Boolean = runCatching {
//    httpClient.delete("$BASE_URL/orders/$id") { auth() }.status.isSuccess()
//}.getOrDefault(false)

// ── Responses ─────────────────────────────────────────────────────────────────

suspend fun apiInsertResponse(body: Map<String, Any?>): OrderResponse? = runCatching {
    httpClient.post("$BASE_URL/responses/insert") {
        auth(); contentType(ContentType.Application.Json); setBody(body)
    }.body<BaseResponse<OrderResponse>>().data
}.getOrNull()

//suspend fun apiAcceptResponse(id: Long): Boolean = runCatching {
//    httpClient.post("$BASE_URL/responses/accept-response") {
//        auth(); contentType(ContentType.Application.Json); setBody(mapOf("id" to id))
//    }.status.isSuccess()
//}.getOrDefault(false)
//
//suspend fun apiCancelResponse(id: Long): Boolean = runCatching {
//    httpClient.post("$BASE_URL/responses/cancel-response") {
//        auth(); contentType(ContentType.Application.Json); setBody(mapOf("id" to id))
//    }.status.isSuccess()
//}.getOrDefault(false)

// ── Messages ──────────────────────────────────────────────────────────────────

//suspend fun apiGetChatList(): List<ChatContact> = runCatching {
//    httpClient.get("$BASE_URL/messages/chat-list") { auth() }
//        .body<BaseResponse<List<ChatContact>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiGetConversation(userId: Int): List<ShowAllMessagesResponse> = runCatching {
//    httpClient.get("$BASE_URL/messages/conversation/$userId") { auth() }
//        .body<BaseResponse<List<ShowAllMessagesResponse>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiSendMessage(toUserId: Int, text: String): InsertResponse? = runCatching {
//    httpClient.post("$BASE_URL/messages/insert") {
//        auth(); contentType(ContentType.Application.Json)
//        setBody(InsertMessage(toUserId = toUserId, text = text, type = ""))
//    }.body<BaseResponse<InsertResponse>>().data
//}.getOrNull()

// ── Notifications ─────────────────────────────────────────────────────────────

//suspend fun apiGetNotifications(): List<String> = runCatching {
//    httpClient.get("$BASE_URL/notifications/my") { auth() }
//        .body<BaseResponse<List<String>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiMarkRead(id: Long): Boolean = runCatching {
//    httpClient.post("$BASE_URL/notifications/mark-read/$id") { auth() }.status.isSuccess()
//}.getOrDefault(false)
//
//suspend fun apiMarkAllRead(): Boolean = runCatching {
//    httpClient.post("$BASE_URL/notifications/mark-all-read") { auth() }.status.isSuccess()
//}.getOrDefault(false)
//
//suspend fun apiDeleteNotification(id: Long): Boolean = runCatching {
//    httpClient.delete("$BASE_URL/notifications/$id") { auth() }.status.isSuccess()
//}.getOrDefault(false)

// ── Advertisements ────────────────────────────────────────────────────────────

//suspend fun apiGetAllAds(): List<Ads> = runCatching {
//    httpClient.get("$BASE_URL/advertisements/show-all") { auth() }
//        .body<BaseResponse<List<Ads>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiGetActiveAds(): List<Ads> = runCatching {
//    httpClient.get("$BASE_URL/advertisements/active") { auth() }
//        .body<BaseResponse<List<Ads>>>().data ?: emptyList()
//}.getOrDefault(emptyList())
//
//suspend fun apiInsertAd(body: Map<String, Any?>): Ads? = runCatching {
//    httpClient.post("$BASE_URL/advertisements/insert") {
//        auth(); contentType(ContentType.Application.Json); setBody(body)
//    }.body<BaseResponse<Ads>>().data
//}.getOrNull()

//suspend fun apiUpdateAd(body: Map<String, Any?>): Ads? = runCatching {
//    httpClient.put("$BASE_URL/advertisements/update") {
//        auth(); contentType(ContentType.Application.Json); setBody(body)
//    }.body<BaseResponse<Ads>>().data
//}.getOrNull()

//suspend fun apiDeleteAd(id: Long): Boolean = runCatching {
//    httpClient.delete("$BASE_URL/advertisements/$id") { auth() }.status.isSuccess()
//}.getOrDefault(false)

//suspend fun apiTrackClick(id: Long) = runCatching {
//    httpClient.post("$BASE_URL/advertisements/track-click/$id") { auth() }
//}.getOrNull()

// ── Services ──────────────────────────────────────────────────────────────────

//suspend fun apiGetAllServices(): List<Service> = runCatching {
//    httpClient.get("$BASE_URL/services/show-all") { auth() }
//        .body<BaseResponse<List<Service>>>().data ?: emptyList()
//}.getOrDefault(emptyList())

//suspend fun apiInsertService(name: String): Service? = runCatching {
//    httpClient.post("$BASE_URL/services/insert") {
//        auth(); contentType(ContentType.Application.Json); setBody(mapOf("name" to name))
//    }.body<BaseResponse<Service>>().data
//}.getOrNull()

//suspend fun apiUpdateService(id: Long, name: String): Service? = runCatching {
//    httpClient.put("$BASE_URL/services/update") {
//        auth(); contentType(ContentType.Application.Json); setBody(
//        mapOf(
//            "id" to id,
//            "name" to name
//        )
//    )
//    }.body<BaseResponse<Service>>().data
//}.getOrNull()

//suspend fun apiDeleteService(id: Long): Boolean = runCatching {
//    httpClient.delete("$BASE_URL/services/$id") { auth() }.status.isSuccess()
//}.getOrDefault(false)

// ── Search ────────────────────────────────────────────────────────────────────

//suspend fun apiSearch(q: String): SearchModel = runCatching {
//    httpClient.get("$BASE_URL/search") { auth(); parameter("q", q) }
//        .body<BaseResponse<SearchModel>>().data ?: SearchModel()
//}.getOrDefault(SearchModel())

// ── Provider Requests ─────────────────────────────────────────────────────────

//suspend fun apiGetAllRequests(): List<ProviderRequest> = runCatching {
//    httpClient.get("$BASE_URL/provider-requests/show-all") { auth() }
//        .body<BaseResponse<List<ProviderRequest>>>().data ?: emptyList()
//}.getOrDefault(emptyList())

//suspend fun apiAcceptRequest(id: Int): Boolean = runCatching {
//    httpClient.post("$BASE_URL/provider-requests/accept/$id") { auth() }.status.isSuccess()
//}.getOrDefault(false)

// ── Image upload ──────────────────────────────────────────────────────────────

suspend fun apiUploadImage(file: File): String? {
    val token = AppState.token ?: return null
    return runCatching {
        val formData = FormData()
        formData.append("file", file, file.name)
        suspendCoroutine<String?> { cont ->
            val xhr = XMLHttpRequest()
            xhr.open("POST", "$BASE_URL/users/upload-image")
            xhr.setRequestHeader("Authorization", "Bearer $token")
            xhr.onload = {
                if (xhr.status.toInt() in 200..299) {
                    try {
                        val parsed: dynamic = JSON.parse(xhr.responseText)
                        val data: String? = parsed.data?.toString()
                        cont.resumeWith(Result.success(data))
                    } catch (_: Exception) {
                        cont.resumeWith(Result.success(null))
                    }
                } else cont.resumeWith(Result.success(null))
                null
            }
            xhr.onerror = { cont.resumeWith(Result.success(null)); null }
            xhr.send(formData)
        }
    }.getOrNull()
}


suspend fun apiUploadImageBackground(file: File): String? {
    val token = AppState.token ?: return null
    return runCatching {
        val formData = FormData()
        formData.append("file", file, file.name)
        suspendCoroutine<String?> { cont ->
            val xhr = XMLHttpRequest()
            xhr.open("POST", "$BASE_URL/users/upload-background-image")
            xhr.setRequestHeader("Authorization", "Bearer $token")
            xhr.onload = {
                if (xhr.status.toInt() in 200..299) {
                    try {
                        val parsed: dynamic = JSON.parse(xhr.responseText)
                        val data: String? = parsed.data?.toString()
                        cont.resumeWith(Result.success(data))
                    } catch (_: Exception) {
                        cont.resumeWith(Result.success(null))
                    }
                } else cont.resumeWith(Result.success(null))
                null
            }
            xhr.onerror = { cont.resumeWith(Result.success(null)); null }
            xhr.send(formData)
        }
    }.getOrNull()
}