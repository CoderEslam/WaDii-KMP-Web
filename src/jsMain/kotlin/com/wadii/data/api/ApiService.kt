package com.wadii.data.api


import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.ads.InsertAds
import com.wadii.domain.model.auth.login.LoginRequest
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.auth.register.RegisterRequest
import com.wadii.domain.model.call.livekit.LiveKitTokenRequest
import com.wadii.domain.model.call.livekit.LiveKitTokenResponse
import com.wadii.domain.model.carTypes.CarType
import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.chat.InsertMessage
import com.wadii.domain.model.chat.InsertResponse
import com.wadii.domain.model.chat.PageMessages
import com.wadii.domain.model.chat.ShowAllMessagesResponse
import com.wadii.domain.model.city.City
import com.wadii.domain.model.city.InsertCity
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.country.InsertCountry
import com.wadii.domain.model.follow.FollowProviderResponse
import com.wadii.domain.model.follow.Followers
import com.wadii.domain.model.offers.NewOfferRequest
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.offers.SavedOfferRequest
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.domain.model.order.CancelReason
import com.wadii.domain.model.order.InsertReason
import com.wadii.domain.model.order.OrderCallbackResponse
import com.wadii.domain.model.order.OrderCancelRequest
import com.wadii.domain.model.order.OrderModel
import com.wadii.domain.model.order.OrderRequest
import com.wadii.domain.model.provider.CreateProviderByAdminRequest
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.provider.ProviderRequest
import com.wadii.domain.model.provider.ProviderRequestCallback
import com.wadii.domain.model.provider.UpdateProviderRequest
import com.wadii.domain.model.providerRequests.ProviderRequestModel
import com.wadii.domain.model.province.InsertProvince
import com.wadii.domain.model.province.Province
import com.wadii.domain.model.response.OrderResponse
import com.wadii.domain.model.response.ResponseCallback
import com.wadii.domain.model.response.ResponseDecision
import com.wadii.domain.model.response.ResponseRequest
import com.wadii.domain.model.serach.SearchModel
import com.wadii.domain.model.service.InsertService
import com.wadii.domain.model.service.Service
import com.wadii.domain.model.user.UpdateUser
import com.wadii.state.AppState
import com.wadii.utils.Constants
import com.wadii.utils.Constants.BASE_URL
import com.wadii.utils.putApiResponse
import com.wadii.domain.model.notification.UserNotification
import com.wadii.domain.model.notification.AgoraTokenRequest
import com.wadii.domain.model.notification.AgoraTokenResponse
import com.wadii.domain.model.notification.ProviderServicesRequest
import com.wadii.domain.model.rate.RateModel
import com.wadii.domain.model.shop.AvailabilityRequest
import com.wadii.domain.model.rate.RateRequest
import com.wadii.domain.model.shop.BranchRequest
import com.wadii.domain.model.shop.CommonSparePart
import com.wadii.domain.model.shop.LinkRequest
import com.wadii.domain.model.shop.NameRequest
import com.wadii.domain.model.shop.WorkTimeRequest
import com.wadii.utils.RequestState
import com.wadii.utils.deleteApiResponse
import com.wadii.utils.getApiResponse
import com.wadii.utils.postApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.parameter
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.InternalAPI
import org.w3c.files.File
import org.w3c.xhr.FormData
import org.w3c.xhr.XMLHttpRequest
import kotlin.coroutines.suspendCoroutine


class ApiService(
    private var client: HttpClient,
    private val httpClientSendFile: HttpClient
) {

    suspend fun login(
        loginRequest: LoginRequest,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<LoginRequest, BaseResponse<User>>(
                urlString = Constants.LOGIN,
                body = loginRequest
            )
        )
    }

    suspend fun register(
        registerRequest: RegisterRequest,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<RegisterRequest, BaseResponse<User>>(
                urlString = Constants.REGISTER,
                body = registerRequest
            )
        )
    }

    //users
    suspend fun updateUser(
        updateUser: UpdateUser,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<UpdateUser, BaseResponse<User>>(
                urlString = Constants.UPDATE_USER,
                body = updateUser
            )
        )
    }

    suspend fun userMe(
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.getApiResponse<BaseResponse<User>>(
                urlString = Constants.USER_ME,
            )
        )
    }

    @OptIn(InternalAPI::class)
    suspend fun updateImageUser(
        byteArray: ByteArray,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            httpClientSendFile.postApiResponse<MultiPartFormDataContent, BaseResponse<User>>(
                urlString = Constants.UPDATE_USER_IMAGE,
                body = MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            byteArray,
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"image_${GMTDate().timestamp}.jpg\""
                                )
                            }
                        )
                    }
                )
            )
        )
    }

    @OptIn(InternalAPI::class)
    suspend fun updateImageBackgroundUser(
        byteArray: ByteArray,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            httpClientSendFile.postApiResponse<MultiPartFormDataContent, BaseResponse<User>>(
                urlString = Constants.UPDATE_USER_BACKGROUND_IMAGE,
                body = MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            byteArray,
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"image_${GMTDate().timestamp}.jpg\""
                                )
                            }
                        )
                    }
                )
            )
        )
    }

    //delete account
    suspend fun deleteAccount(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.DELETE_ACCOUNT(id)))
    }


    //providers
    suspend fun providersList(response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<ProviderModel>>>(urlString = Constants.PROVIDER_SHOW_ALL))
    }

    suspend fun providersListByServiceId(
        id: Long,
        response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<ProviderModel>>>(urlString = "${Constants.PROVIDER_SHOW_ALL_BY_SERVICE_ID}/$id"))
    }

    suspend fun getProviderById(
        id: Int,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<ProviderModel>>(urlString = "${Constants.PROVIDER_SHOW_BY_ID}/$id"))
    }

    suspend fun providerMe(
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<ProviderModel>>(urlString = Constants.PROVIDER_ME))
    }

    suspend fun getFollowerList(
        id: Int,
        response: (RequestState<BaseResponse<List<Followers>>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.getApiResponse<BaseResponse<List<Followers>>>(
                urlString = Constants.GET_LIST_FOLLOWER_PROVIDER(
                    id
                )
            )
        )
    }

    suspend fun followProvider(
        providerId: Long,
        response: (RequestState<BaseResponse<FollowProviderResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<FollowProviderResponse>>(urlString = "${Constants.FOLLOW_PROVIDER_BY_ID}/$providerId"))
    }

    suspend fun unfollowProvider(
        providerId: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = "${Constants.UNFOLLOW_PROVIDER_BY_ID}/$providerId"))
    }

    suspend fun updateProvider(
        request: UpdateProviderRequest,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<UpdateProviderRequest, BaseResponse<ProviderModel>>(
                urlString = Constants.UPDATE_PROVIDER(request.id),
                body = request
            )
        )
    }

    suspend fun createProviderByAdmin(
        request: CreateProviderByAdminRequest,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<CreateProviderByAdminRequest, BaseResponse<ProviderModel>>(
                urlString = Constants.CREATE_PROVIDER_BY_ADMIN,
                body = request
            )
        )
    }

    //provider-requests
    @OptIn(InternalAPI::class)
    suspend fun requestProvider(
        providerRequest: ProviderRequest,
        response: (RequestState<BaseResponse<ProviderRequestCallback>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            httpClientSendFile.postApiResponse<MultiPartFormDataContent, BaseResponse<ProviderRequestCallback>>(
                urlString = Constants.PROVIDER_REQUEST_ASK_BE_PROVIDER,
                body = MultiPartFormDataContent(
                    formData {
                        append("name", providerRequest.name)
                        append("userId", providerRequest.userId.toString())
                        append("address", providerRequest.address)
                        append("phoneNumber", providerRequest.phoneNumber)
                        providerRequest.serviceIds.forEach { id ->
                            append("serviceIds[]", id)
                        }
                        providerRequest.links.forEach { link ->
                            append("links[]", link)
                        }
                        providerRequest.frontIdImage?.let { bytes ->
                            append(
                                "frontIdImage",
                                bytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"front_id_${GMTDate().timestamp}.jpg\""
                                    )
                                }
                            )
                        }
                        providerRequest.backIdImage?.let { bytes ->
                            append(
                                "backIdImage",
                                bytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"back_id_${GMTDate().timestamp}.jpg\""
                                    )
                                }
                            )
                        }
                        providerRequest.taxCardFront?.let { bytes ->
                            append(
                                "taxCardFront",
                                bytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"tax_card_front_${GMTDate().timestamp}.jpg\""
                                    )
                                }
                            )
                        }
                        providerRequest.taxCardBack?.let { bytes ->
                            append(
                                "taxCardBack",
                                bytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"tax_card_back_${GMTDate().timestamp}.jpg\""
                                    )
                                }
                            )
                        }
                    }
                )
            )
        )
    }


    suspend fun putItUser(
        id: Long,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<BaseResponse<User>>(
                urlString = Constants.PROVIDER_REQUEST_PUT_IT_USER(id)
            )
        )
    }

    suspend fun putItProvider(
        id: Long,
        response: (RequestState<BaseResponse<User>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<BaseResponse<User>>(
                urlString = Constants.PROVIDER_REQUEST_PUT_IT_PROVIDER(id)
            )
        )
    }


    //service
    suspend fun getServiceList(response: (RequestState<BaseResponse<List<Service>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Service>>>(urlString = Constants.SERVICE_LIST))
    }

    suspend fun addService(
        insertService: InsertService,
        response: (RequestState<BaseResponse<Service>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertService, BaseResponse<Service>>(
                urlString = Constants.SERVICE_INSERT,
                body = insertService
            )
        )
    }

    suspend fun updateService(
        insertService: InsertService,
        response: (RequestState<BaseResponse<Service>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertService, BaseResponse<Service>>(
                urlString = Constants.SERVICE_UPDATE,
                body = insertService
            )
        )
    }

    suspend fun deleteService(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.deleteApiResponse<BaseResponse<Boolean>>(
                urlString = Constants.SERVICE_DELETE(id),
            )
        )
    }

    suspend fun getResponseList(response: (RequestState<BaseResponse<List<OrderResponse>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<OrderResponse>>>(urlString = Constants.RESPONSE_SHOW_ALL_RESPONSES_OF_USER))
    }

    suspend fun acceptResponse(
        request: ResponseDecision,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<ResponseDecision, BaseResponse<OrderResponse>>(
                urlString = Constants.ACCEPT_RESPONSE,
                body = request
            )
        )
    }

    suspend fun rejectResponse(
        request: ResponseDecision,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<ResponseDecision, BaseResponse<OrderResponse>>(
                urlString = Constants.REJECT_RESPONSE,
                body = request
            )
        )
    }

    suspend fun createResponse(
        request: ResponseRequest,
        response: (RequestState<BaseResponse<ResponseCallback>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<ResponseRequest, BaseResponse<ResponseCallback>>(
                urlString = Constants.CREATE_RESPONSE,
                body = request
            )
        )
    }

    //order
    suspend fun showAll(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<OrderModel>>>(urlString = Constants.ORDER_SHOW_ALL))
    }

    suspend fun showAllOrderOfUser(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<OrderModel>>>(urlString = Constants.ORDER_SHOW_ALL_ORDER_OF_USER))
    }

    suspend fun showAllOrderOfProvider(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<OrderModel>>>(urlString = Constants.SHOW_ALL_ORDER_OF_PROVIDER))
    }

    suspend fun insertOrder(
        orderRequest: OrderRequest,
        response: (RequestState<BaseResponse<OrderCallbackResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<OrderRequest, BaseResponse<OrderCallbackResponse>>(
                urlString = Constants.ORDER_INSERT,
                body = orderRequest
            )
        )
    }

    suspend fun cancelOrder(
        request: OrderCancelRequest,
        response: (RequestState<BaseResponse<OrderCallbackResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<OrderCancelRequest, BaseResponse<OrderCallbackResponse>>(
                urlString = Constants.ORDER_CANCEL,
                body = request
            )
        )
    }

    suspend fun getCancelReasons(response: (RequestState<BaseResponse<List<CancelReason>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<CancelReason>>>(urlString = Constants.ORDER_CANCEL_REASONS_LIST))
    }

    suspend fun addReason(
        insertReason: InsertReason,
        response: (RequestState<BaseResponse<CancelReason>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertReason, BaseResponse<CancelReason>>(
                urlString = Constants.REASON_INSERT,
                body = insertReason
            )
        )
    }

    suspend fun updateReason(
        insertReason: InsertReason,
        response: (RequestState<BaseResponse<CancelReason>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertReason, BaseResponse<CancelReason>>(
                urlString = Constants.REASON_UPDATE,
                body = insertReason
            )
        )
    }

    suspend fun deleteReason(
        id: Int,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.deleteApiResponse<BaseResponse<Boolean>>(
                urlString = Constants.REASON_DELETE(id),
            )
        )
    }

    //message
    suspend fun chatList(response: (RequestState<BaseResponse<List<ChatContact>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<ChatContact>>>(urlString = Constants.CHAT_LIST))
    }

    suspend fun insertMessage(
        insertMessage: InsertMessage,
        response: (RequestState<BaseResponse<InsertResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertMessage, BaseResponse<InsertResponse>>(
                urlString = Constants.CHAT_SEND_MESSAGE,
                body = insertMessage
            )
        )
    }

    suspend fun showAllMessages(response: (RequestState<BaseResponse<List<ShowAllMessagesResponse>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<ShowAllMessagesResponse>>>(urlString = Constants.SHOW_ALL_MESSAGES))
    }

    suspend fun conversation(
        userId: Long,
        page: Int = 0,
        response: (RequestState<BaseResponse<PageMessages>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<PageMessages>>(urlString = "${Constants.CONVERSATION}/$userId") {
            parameter("page", page)
        })
    }

    //livekit
    suspend fun getLiveKitToken(
        request: LiveKitTokenRequest,
        response: (RequestState<BaseResponse<LiveKitTokenResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<LiveKitTokenRequest, BaseResponse<LiveKitTokenResponse>>(
                urlString = Constants.LIVEKIT_TOKEN,
                body = request
            )
        )
    }

    //offers
    suspend fun getOffersList(response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<OfferResponse>>>(urlString = Constants.OFFERS_SHOW_ALL))
    }

    suspend fun getOffersByServiceId(
        id: Long,
        response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<OfferResponse>>>(urlString = "${Constants.OFFERS_SHOW_ALL_BY_SERVICE_ID}/$id"))
    }

    suspend fun insertOffer(
        request: SavedOfferRequest,
        response: (RequestState<BaseResponse<SavedOffer>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<SavedOfferRequest, BaseResponse<SavedOffer>>(
                urlString = Constants.INSERT_OFFER,
                body = request
            )
        )
    }

    suspend fun removeSavedOffer(
        offerId: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = "${Constants.REMOVE_SAVED_OFFER}/$offerId"))
    }

    suspend fun getMySavedOffers(response: (RequestState<BaseResponse<List<SavedOffer>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<SavedOffer>>>(urlString = Constants.MY_SAVED_OFFERS))
    }

    suspend fun createOffer(
        request: NewOfferRequest,
        response: (RequestState<BaseResponse<OfferResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<NewOfferRequest, BaseResponse<OfferResponse>>(
                urlString = Constants.CREATE_OFFER,
                body = request
            )
        )
    }

    suspend fun updateOffer(
        offerId: Int,
        request: NewOfferRequest,
        response: (RequestState<BaseResponse<OfferResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<NewOfferRequest, BaseResponse<OfferResponse>>(
                urlString = Constants.UPDATE_OFFER, // POST /offers/update takes the id in the body
                body = request
            )
        )
    }

    suspend fun deleteOffer(
        offerId: Int,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = "${Constants.DELETE_OFFER}/$offerId"))
    }

    //car type
    suspend fun getCarTypeList(response: (RequestState<BaseResponse<List<CarType>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<CarType>>>(urlString = Constants.CAR_TYPE_SHOW_ALL))
    }

    //country
    suspend fun getCountryList(response: (RequestState<BaseResponse<List<Country>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Country>>>(urlString = Constants.COUNTRY_SHOW_ALL))
    }

    suspend fun getProvinceByCountryId(
        countryId: Long,
        response: (RequestState<BaseResponse<List<Province>>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Province>>>(urlString = "${Constants.PROVINCE_BY_COUNTRY_ID}/$countryId"))
    }

    suspend fun getCitiesByProvinceId(
        provinceId: Long,
        response: (RequestState<BaseResponse<List<City>>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<City>>>(urlString = "${Constants.CITY_BY_PROVINCE_ID}/$provinceId"))
    }

    suspend fun insertCountry(
        insertCountry: InsertCountry,
        response: (RequestState<BaseResponse<Country>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertCountry, BaseResponse<Country>>(
                urlString = Constants.COUNTRY_INSERT,
                body = insertCountry
            )
        )
    }

    suspend fun updateCountry(
        insertCountry: InsertCountry,
        response: (RequestState<BaseResponse<Country>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertCountry, BaseResponse<Country>>(
                urlString = Constants.COUNTRY_UPDATE,
                body = insertCountry
            )
        )
    }

    suspend fun deleteCountry(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.COUNTRY_DELETE(id)))
    }

    //province
    suspend fun getProvinceList(response: (RequestState<BaseResponse<List<Province>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Province>>>(urlString = Constants.PROVINCE_SHOW_ALL))
    }

    suspend fun insertProvince(
        insertProvince: InsertProvince,
        response: (RequestState<BaseResponse<Province>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertProvince, BaseResponse<Province>>(
                urlString = Constants.PROVINCE_INSERT,
                body = insertProvince
            )
        )
    }

    suspend fun updateProvince(
        insertProvince: InsertProvince,
        response: (RequestState<BaseResponse<Province>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertProvince, BaseResponse<Province>>(
                urlString = Constants.PROVINCE_UPDATE,
                body = insertProvince
            )
        )
    }

    suspend fun deleteProvince(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.PROVINCE_DELETE(id)))
    }

    //city
    suspend fun getCityList(response: (RequestState<BaseResponse<List<City>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<City>>>(urlString = Constants.CITY_SHOW_ALL))
    }

    suspend fun insertCity(
        insertCity: InsertCity,
        response: (RequestState<BaseResponse<City>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertCity, BaseResponse<City>>(
                urlString = Constants.CITY_INSERT,
                body = insertCity
            )
        )
    }

    suspend fun updateCity(
        insertCity: InsertCity,
        response: (RequestState<BaseResponse<City>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertCity, BaseResponse<City>>(
                urlString = Constants.CITY_UPDATE,
                body = insertCity
            )
        )
    }

    suspend fun deleteCity(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.CITY_DELETE(id)))
    }

    //notifications
    suspend fun getNotifications(response: (RequestState<BaseResponse<List<UserNotification>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<UserNotification>>>(urlString = Constants.NOTIFICATIONS_SHOW_ALL))
    }

    suspend fun notificationCount(response: (RequestState<BaseResponse<Int>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Int>>(urlString = Constants.NOTIFICATIONS_MY_UNREAD_COUNT))
    }

    //ads
    suspend fun ads(
        response: (RequestState<BaseResponse<List<Ads>>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Ads>>>(urlString = Constants.ADS_SHOW_ALL))
    }


    suspend fun insertAds(
        insertAds: InsertAds,
        response: (RequestState<BaseResponse<Ads>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertAds, BaseResponse<Ads>>(
                urlString = Constants.ADS_INSERT,
                body = insertAds
            )
        )
    }

    suspend fun updateAds(
        insertAds: InsertAds,
        response: (RequestState<BaseResponse<Ads>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<InsertAds, BaseResponse<Ads>>(
                urlString = Constants.ADS_UPDATE,
                body = insertAds
            )
        )
    }

    suspend fun deleteAds(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.deleteApiResponse<BaseResponse<Boolean>>(
                urlString = Constants.ADS_DELETE(id),
            )
        )
    }


    suspend fun search(
        query: String,
        response: (RequestState<BaseResponse<SearchModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.getApiResponse<BaseResponse<SearchModel>>(
                urlString = Constants.SEARCH
            ) {
                parameter("q", query)
            }
        )
    }

    //admin
    suspend fun requests(
        response: (RequestState<BaseResponse<List<ProviderRequestModel>>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.getApiResponse<BaseResponse<List<ProviderRequestModel>>>(
                urlString = Constants.PROVIDER_REQUEST
            )
        )
    }

    suspend fun acceptRequest(
        id: Long,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<BaseResponse<ProviderModel>>(
                urlString = Constants.PROVIDER_REQUEST_ACCEPT(id)
            )
        )
    }

    suspend fun rejectRequest(
        id: Long,
        reason: String?,
        response: (RequestState<BaseResponse<ProviderRequestModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<BaseResponse<ProviderRequestModel>>(
                urlString = Constants.PROVIDER_REQUEST_REJECT(id),
                block = { if (!reason.isNullOrBlank()) parameter("reason", reason.trim()) }
            )
        )
    }

    // ---------------------------------------------------------------------------------------------
    // Endpoints from API_REFERENCE.md. Callers validate the body first (see utils/RequestValidators).
    // ---------------------------------------------------------------------------------------------

    // orders
    suspend fun updateOrder(request: OrderRequest, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<OrderRequest, BaseResponse<OrderModel>>(urlString = Constants.ORDER_UPDATE, body = request))
    }

    suspend fun deleteOrder(id: Int, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.ORDER_DELETE(id)))
    }

    // responses (proposals)
    suspend fun updateResponse(request: ResponseRequest, response: (RequestState<BaseResponse<ResponseCallback>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<ResponseRequest, BaseResponse<ResponseCallback>>(urlString = Constants.RESPONSE_UPDATE, body = request))
    }

    suspend fun deleteResponse(id: Int, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.RESPONSE_DELETE(id)))
    }

    // notifications
    suspend fun markNotificationRead(id: Long, response: (RequestState<BaseResponse<UserNotification>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<UserNotification>>(urlString = Constants.NOTIFICATION_MARK_READ(id)))
    }

    suspend fun markAllNotificationsRead(response: (RequestState<BaseResponse<List<UserNotification>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<List<UserNotification>>>(urlString = Constants.NOTIFICATION_MARK_ALL_READ))
    }

    suspend fun deleteNotification(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.NOTIFICATION_DELETE(id)))
    }

    // rate
    suspend fun insertRate(request: RateRequest, response: (RequestState<BaseResponse<RateModel>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<RateRequest, BaseResponse<RateModel>>(urlString = Constants.RATE_INSERT, body = request))
    }

    suspend fun updateRate(request: RateRequest, response: (RequestState<BaseResponse<RateModel>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<RateRequest, BaseResponse<RateModel>>(urlString = Constants.RATE_UPDATE, body = request))
    }

    suspend fun getAllUsers(response: (RequestState<BaseResponse<List<User>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<User>>>(urlString = Constants.USER_SHOW_ALL))
    }

    suspend fun startOrder(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<OrderModel>>(urlString = Constants.ORDER_START(id)))
    }

    suspend fun completeOrder(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<OrderModel>>(urlString = Constants.ORDER_COMPLETE(id)))
    }

    suspend fun setAvailability(request: AvailabilityRequest, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<AvailabilityRequest, BaseResponse<ProviderModel>>(urlString = Constants.PROVIDER_AVAILABILITY, body = request))
    }

    suspend fun setProviderActive(id: Long, active: Boolean, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<ProviderModel>>(urlString = Constants.PROVIDER_SET_ACTIVE(id, active)))
    }

    suspend fun deleteRate(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.RATE_DELETE(id)))
    }

    // car types
    suspend fun insertCarType(request: NameRequest, response: (RequestState<BaseResponse<com.wadii.domain.model.carTypes.CarType>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<NameRequest, BaseResponse<com.wadii.domain.model.carTypes.CarType>>(urlString = Constants.CAR_TYPE_INSERT, body = request))
    }

    suspend fun updateCarType(request: NameRequest, response: (RequestState<BaseResponse<com.wadii.domain.model.carTypes.CarType>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<NameRequest, BaseResponse<com.wadii.domain.model.carTypes.CarType>>(urlString = Constants.CAR_TYPE_UPDATE, body = request))
    }

    suspend fun deleteCarType(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.CAR_TYPE_DELETE(id)))
    }

    // common spare parts
    suspend fun getCommonSpareParts(response: (RequestState<BaseResponse<List<CommonSparePart>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<CommonSparePart>>>(urlString = Constants.COMMON_SPARE_PARTS_SHOW_ALL))
    }

    suspend fun insertCommonSparePart(request: NameRequest, response: (RequestState<BaseResponse<CommonSparePart>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<NameRequest, BaseResponse<CommonSparePart>>(urlString = Constants.COMMON_SPARE_PARTS_INSERT, body = request))
    }

    suspend fun updateCommonSparePart(request: NameRequest, response: (RequestState<BaseResponse<CommonSparePart>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<NameRequest, BaseResponse<CommonSparePart>>(urlString = Constants.COMMON_SPARE_PARTS_UPDATE, body = request))
    }

    suspend fun deleteCommonSparePart(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.COMMON_SPARE_PARTS_DELETE(id)))
    }

    // provider shop: branches, work times, links, services
    suspend fun branchesByProvider(providerId: Long, response: (RequestState<BaseResponse<List<com.wadii.domain.model.branch.Branche>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<com.wadii.domain.model.branch.Branche>>>(urlString = Constants.BRANCH_BY_PROVIDER(providerId)))
    }

    suspend fun insertBranch(request: BranchRequest, response: (RequestState<BaseResponse<com.wadii.domain.model.branch.Branche>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BranchRequest, BaseResponse<com.wadii.domain.model.branch.Branche>>(urlString = Constants.BRANCH_INSERT, body = request))
    }

    suspend fun updateBranch(request: BranchRequest, response: (RequestState<BaseResponse<com.wadii.domain.model.branch.Branche>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BranchRequest, BaseResponse<com.wadii.domain.model.branch.Branche>>(urlString = Constants.BRANCH_UPDATE, body = request))
    }

    suspend fun deleteBranch(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.BRANCH_DELETE(id)))
    }

    /** Server quirk: this one is a POST with no body. */
    suspend fun workTimesByBranch(branchId: Long, response: (RequestState<BaseResponse<List<com.wadii.domain.model.WorkTime>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<List<com.wadii.domain.model.WorkTime>>>(urlString = Constants.WORK_TIME_BY_BRANCH(branchId)))
    }

    suspend fun insertWorkTime(request: WorkTimeRequest, response: (RequestState<BaseResponse<com.wadii.domain.model.WorkTime>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<WorkTimeRequest, BaseResponse<com.wadii.domain.model.WorkTime>>(urlString = Constants.WORK_TIME_INSERT, body = request))
    }

    suspend fun updateWorkTime(request: WorkTimeRequest, response: (RequestState<BaseResponse<com.wadii.domain.model.WorkTime>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<WorkTimeRequest, BaseResponse<com.wadii.domain.model.WorkTime>>(urlString = Constants.WORK_TIME_UPDATE, body = request))
    }

    suspend fun deleteWorkTime(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.WORK_TIME_DELETE(id)))
    }

    suspend fun insertLink(request: LinkRequest, response: (RequestState<BaseResponse<com.wadii.domain.model.link.Link>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<LinkRequest, BaseResponse<com.wadii.domain.model.link.Link>>(urlString = Constants.LINK_INSERT, body = request))
    }

    suspend fun updateLink(request: LinkRequest, response: (RequestState<BaseResponse<com.wadii.domain.model.link.Link>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<LinkRequest, BaseResponse<com.wadii.domain.model.link.Link>>(urlString = Constants.LINK_UPDATE, body = request))
    }

    suspend fun deleteLink(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.LINK_DELETE(id)))
    }

    suspend fun updateProviderServices(request: ProviderServicesRequest, response: (RequestState<BaseResponse<ProviderModel>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<ProviderServicesRequest, BaseResponse<ProviderModel>>(urlString = Constants.PROVIDER_UPDATE_SERVICES, body = request))
    }

    suspend fun providersByServices(serviceIds: List<Long>, response: (RequestState<BaseResponse<List<ProviderModel>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<ProviderModel>>>(urlString = Constants.PROVIDER_FILTER_BY_SERVICES) {
            parameter("serviceIds", serviceIds.joinToString(","))
        })
    }

    suspend fun offersByServices(serviceIds: List<Long>, response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<OfferResponse>>>(urlString = Constants.OFFERS_FILTER_BY_SERVICES) {
            parameter("serviceIds", serviceIds.joinToString(","))
        })
    }

    // ads
    suspend fun setAdStatus(id: Long, status: String, response: (RequestState<BaseResponse<Ads>>) -> Unit) {
        response(RequestState.Loading)
        response(client.putApiResponse<Unit, BaseResponse<Ads>>(urlString = Constants.ADS_STATUS(id, status)))
    }

    suspend fun trackAdImpression(id: Long, response: (RequestState<BaseResponse<Ads>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<Ads>>(urlString = Constants.TRACK_IMPRESSION(id)))
    }

    suspend fun trackAdClick(id: Long, response: (RequestState<BaseResponse<Ads>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<Ads>>(urlString = Constants.TRACK_CLICK(id)))
    }

    // users, presence, calls
    suspend fun unreadMessageCount(response: (RequestState<BaseResponse<Int>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Int>>(urlString = Constants.USER_UNREAD_MESSAGES))
    }

    suspend fun isUserOnline(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Boolean>>(urlString = Constants.USER_ONLINE(id)))
    }

    suspend fun presence(userIds: List<Long>, response: (RequestState<BaseResponse<Map<String, Boolean>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Map<String, Boolean>>>(urlString = Constants.PRESENCE) {
            parameter("userIds", userIds.distinct().joinToString(","))
        })
    }

    suspend fun onlineUsers(response: (RequestState<BaseResponse<List<Long>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Long>>>(urlString = Constants.PRESENCE_ONLINE))
    }

    suspend fun agoraToken(request: AgoraTokenRequest, response: (RequestState<BaseResponse<AgoraTokenResponse>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<AgoraTokenRequest, BaseResponse<AgoraTokenResponse>>(urlString = Constants.AGORA_TOKEN, body = request))
    }

    suspend fun getOrderById(
        id: Int,
        response: (RequestState<BaseResponse<OrderModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.getApiResponse<BaseResponse<OrderModel>>(
                urlString = Constants.ORDER_SHOW_BY_ID(
                    id
                )
            )
        )
    }

//    suspend fun apiUploadImage(file: File): String? {
//        val token = AppState.token ?: return null
//        return runCatching {
//            val formData = FormData()
//            formData.append("file", file, file.name)
//            suspendCoroutine<String?> { cont ->
//                val xhr = XMLHttpRequest()
//                xhr.open("POST", "$BASE_URL/users/upload-image")
//                xhr.setRequestHeader("Authorization", "Bearer $token")
//                xhr.onload = {
//                    if (xhr.status.toInt() in 200..299) {
//                        try {
//                            val parsed: dynamic = JSON.parse(xhr.responseText)
//                            val data: String? = parsed.data?.toString()
//                            cont.resumeWith(Result.success(data))
//                        } catch (_: Exception) {
//                            cont.resumeWith(Result.success(null))
//                        }
//                    } else cont.resumeWith(Result.success(null))
//                    null
//                }
//                xhr.onerror = { cont.resumeWith(Result.success(null)); null }
//                xhr.send(formData)
//            }
//        }.getOrNull()
//    }
//
//    suspend fun apiUploadImageBackground(file: File): User {
//        val token = AppState.token ?: return null
//        return runCatching {
//            val formData = FormData()
//            formData.append("file", file, file.name)
//            suspendCoroutine<User> { cont ->
//                val xhr = XMLHttpRequest()
//                xhr.open("POST", "$BASE_URL/users/upload-background-image")
//                xhr.setRequestHeader("Authorization", "Bearer $token")
//                xhr.onload = {
//                    if (xhr.status.toInt() in 200..299) {
//                        try {
//                            val parsed: dynamic = JSON.parse(xhr.responseText)
//                            val data: String? = parsed.data?.toString()
//                            cont.resumeWith(Result.success(data))
//                        } catch (_: Exception) {
//                            cont.resumeWith(Result.success(null))
//                        }
//                    } else cont.resumeWith(Result.success(null))
//                    null
//                }
//                xhr.onerror = { cont.resumeWith(Result.success(null)); null }
//                xhr.send(formData)
//            }
//        }.getOrNull()
//    }

}
