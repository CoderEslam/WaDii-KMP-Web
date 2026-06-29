package com.wadii.data.api



import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.auth.login.LoginRequest
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.auth.register.RegisterRequest
import com.wadii.domain.model.carTypes.CarType
import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.chat.InsertMessage
import com.wadii.domain.model.chat.InsertResponse
import com.wadii.domain.model.chat.PageMessages
import com.wadii.domain.model.chat.ShowAllMessagesResponse
import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.follow.FollowProviderResponse
import com.wadii.domain.model.follow.Followers
import com.wadii.domain.model.offers.NewOfferRequest
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.offers.SavedOfferRequest
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.domain.model.order.OrderCallbackResponse
import com.wadii.domain.model.order.OrderModel
import com.wadii.domain.model.order.OrderRequest
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.provider.ProviderRequest
import com.wadii.domain.model.provider.ProviderRequestCallback
import com.wadii.domain.model.provider.UpdateProviderRequest
import com.wadii.domain.model.providerRequests.ProviderRequestModel
import com.wadii.domain.model.province.Province
import com.wadii.domain.model.response.OrderResponse
import com.wadii.domain.model.response.ResponseCallback
import com.wadii.domain.model.response.ResponseRequest
import com.wadii.domain.model.serach.SearchModel
import com.wadii.domain.model.service.Service
import com.wadii.domain.model.user.UpdateUser
import com.wadii.utils.Constants
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
    suspend fun deleteAccount(response: (RequestState<String>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<String>(urlString = Constants.DELETE_ACCOUNT))
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
        providerId: Int,
        response: (RequestState<BaseResponse<FollowProviderResponse>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<FollowProviderResponse>>(urlString = "${Constants.FOLLOW_PROVIDER_BY_ID}/$providerId"))
    }

    suspend fun unfollowProvider(
        providerId: Int,
        response: (RequestState<BaseResponse<String>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<String>>(urlString = "${Constants.UNFOLLOW_PROVIDER_BY_ID}/$providerId"))
    }

    suspend fun updateProvider(
        request: UpdateProviderRequest,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<UpdateProviderRequest, BaseResponse<ProviderModel>>(
                urlString = "${Constants.UPDATE_PROVIDER}/${request.id}",
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
                    }
                )
            )
        )
    }


    suspend fun putItUser(
        id: Int,
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
        id: Int,
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


    //response
//    suspend fun getResponseList(response: (RequestState<BaseResponse<List<OrderResponse>>>) -> Unit) {
//        response(RequestState.Loading)
//        response(client.getApiResponse<BaseResponse<List<OrderResponse>>>(urlString = Constants.RESPONSE_SHOW_ALL))
//    }

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
        userId: Int,
        page: Int = 0,
        response: (RequestState<BaseResponse<PageMessages>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<PageMessages>>(urlString = "${Constants.CONVERSATION}/$userId") {
            parameter("page", page)
        })
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
        response: (RequestState<BaseResponse<String>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<String>>(urlString = "${Constants.REMOVE_SAVED_OFFER}/$offerId"))
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
        response: (RequestState<BaseResponse<String>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<String>>(urlString = "${Constants.DELETE_OFFER}/$offerId"))
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
        countryId: Int,
        response: (RequestState<BaseResponse<List<Province>>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Province>>>(urlString = "${Constants.PROVINCE_BY_COUNTRY_ID}/$countryId"))
    }

    suspend fun getCitiesByProvinceId(
        provinceId: Int,
        response: (RequestState<BaseResponse<List<City>>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<City>>>(urlString = "${Constants.CITY_BY_PROVINCE_ID}/$provinceId"))
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

    suspend fun getOrderById(
        id: Int,
        response: (RequestState<BaseResponse<OrderModel>>) -> Unit
    ) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<OrderModel>>(urlString = Constants.ORDER_SHOW_BY_ID(id)))
    }

}
