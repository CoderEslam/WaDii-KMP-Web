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
import com.wadii.domain.model.response.ResponseRequest
import com.wadii.domain.model.serach.SearchModel
import com.wadii.domain.model.service.InsertService
import com.wadii.domain.model.service.Service
import com.wadii.domain.model.user.UpdateUser
import com.wadii.state.AppState
import com.wadii.utils.Constants
import com.wadii.utils.Constants.BASE_URL
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
    suspend fun deleteAccount(response: (RequestState<Boolean>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<Boolean>(urlString = Constants.DELETE_ACCOUNT))
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
        request: OrderResponse,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<OrderResponse, BaseResponse<OrderResponse>>(
                urlString = Constants.ACCEPT_RESPONSE,
                body = request
            )
        )
    }

    suspend fun rejectResponse(
        request: OrderResponse,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<OrderResponse, BaseResponse<OrderResponse>>(
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
                urlString = "${Constants.UPDATE_OFFER}/$offerId",
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
    suspend fun getNotifications(response: (RequestState<BaseResponse<List<String>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<String>>>(urlString = Constants.NOTIFICATIONS_SHOW_ALL))
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
        response: (RequestState<BaseResponse<ProviderRequestModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<BaseResponse<ProviderRequestModel>>(
                urlString = Constants.PROVIDER_REQUEST_REJECT(id)
            )
        )
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
