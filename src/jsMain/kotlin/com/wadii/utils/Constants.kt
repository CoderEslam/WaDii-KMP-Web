package com.wadii.utils

object Constants {

    const val TOKEN_KEY = "TOKEN_KEY"
    const val DEVELOPMENT_MODE = true
    const val APP_LANGUAGE = "APP_LANGUAGE"
    const val USER_KEY = "USER_KEY"
    const val THEME_KEY = "THEME_KEY"
    const val IP = "172.28.0.123"
    const val BASE_URL = "http://$IP:8080"
    const val BASE_URL_USER_IMAGES = "${BASE_URL}/users"
    const val VERSION = ""
    const val euroSign = "\u20AC"
    const val dollarSign = "\u0024"
    const val percentageSign = "%"
    const val BASE64_IDENTIFIER = "data:image/jpg;base64,"

    //AUTH
    const val REGISTER = "${BASE_URL}$VERSION/auth/register"
    const val LOGIN = "${BASE_URL}$VERSION/auth/login"

    //user
    const val LOGOUT = "${BASE_URL}$VERSION/users/logout"

    const val UPDATE_USER = "${BASE_URL}$VERSION/users/update"

    const val USER_ME = "${BASE_URL}$VERSION/users/me"

    const val UPDATE_USER_IMAGE = "${BASE_URL}$VERSION/users/upload-image"
    const val UPDATE_USER_BACKGROUND_IMAGE = "${BASE_URL}$VERSION/users/upload-background-image"

    //providers
    const val PROVIDER_SHOW_ALL = "${BASE_URL}$VERSION/providers/show-all"
    const val PROVIDER_SHOW_ALL_BY_SERVICE_ID = "${BASE_URL}$VERSION/providers/filter-by-service"
    const val PROVIDER_SHOW_BY_ID = "${BASE_URL}$VERSION/providers/show"
    const val PROVIDER_ME = "${BASE_URL}$VERSION/providers/me"

    fun GET_LIST_FOLLOWER_PROVIDER(id: Int) = "${BASE_URL}$VERSION/providers/$id/followers"

    const val FOLLOW_PROVIDER_BY_ID = "${BASE_URL}$VERSION/providers/follow-provider"
    const val UNFOLLOW_PROVIDER_BY_ID = "${BASE_URL}$VERSION/providers/unfollow-provider"

    //provider-requests
    const val PROVIDER_REQUEST_ASK_BE_PROVIDER = "${BASE_URL}$VERSION/provider-requests/request"

    fun PROVIDER_REQUEST_PUT_IT_PROVIDER(id: Int) =
        "${BASE_URL}$VERSION/provider-requests/put-it-provider/$id"

    fun PROVIDER_REQUEST_PUT_IT_USER(id: Int) =
        "${BASE_URL}$VERSION/provider-requests/put-it-user/$id"

    //service
    const val SERVICE_LIST = "${BASE_URL}$VERSION/services/show-all"

    const val UPDATE_PROVIDER = "${BASE_URL}$VERSION/providers/update-all"

    //specialties
    const val SPECIALTIES_LIST = "${BASE_URL}$VERSION/specialties/list"

    //provinces
    const val PROVINCES_LIST = "${BASE_URL}$VERSION/provinces/list"

    //response
    const val RESPONSE_SHOW_ALL = "${BASE_URL}$VERSION/responses/show-all"
    const val RESPONSE_SHOW_ALL_RESPONSES_OF_USER = "${BASE_URL}$VERSION/responses/get-all-response-of-user"
    const val ACCEPT_RESPONSE = "${BASE_URL}$VERSION/responses/accept-response"
    const val REJECT_RESPONSE = "${BASE_URL}$VERSION/responses/cancel-response"
    const val CREATE_RESPONSE = "${BASE_URL}$VERSION/responses/insert"


    //order
    const val ORDER_SHOW_ALL = "${BASE_URL}$VERSION/orders/show-all"
    const val ORDER_SHOW_ALL_ORDER_OF_USER = "${BASE_URL}$VERSION/orders/show-all-order-of-user"
    const val ORDER_INSERT = "${BASE_URL}$VERSION/orders/insert"
    const val SHOW_ALL_ORDER_OF_PROVIDER = "${BASE_URL}$VERSION/orders/show-all-order-of-provider"

    //offers
    const val OFFERS_SHOW_ALL = "${BASE_URL}$VERSION/offers/show-all"
    const val OFFERS_SHOW_ALL_BY_SERVICE_ID = "${BASE_URL}$VERSION/offers/filter-by-service"
    const val INSERT_OFFER = "${BASE_URL}$VERSION/saved-offers/insert"
    const val REMOVE_SAVED_OFFER = "${BASE_URL}$VERSION/saved-offers/remove"
    const val MY_SAVED_OFFERS = "${BASE_URL}$VERSION/saved-offers/my-saved-offers"
    const val CREATE_OFFER = "${BASE_URL}$VERSION/offers/create"
    const val UPDATE_OFFER = "${BASE_URL}$VERSION/offers/update"
    const val DELETE_OFFER = "${BASE_URL}$VERSION/offers/delete"


    //message
    const val CHAT_LIST = "${BASE_URL}$VERSION/messages/chat-list"
    const val CHAT_SEND_MESSAGE = "${BASE_URL}$VERSION/messages/insert"
    const val SHOW_ALL_MESSAGES = "${BASE_URL}$VERSION/messages/show-all"

    const val CONVERSATION = "${BASE_URL}$VERSION/messages/conversation"

    //car type
    const val CAR_TYPE_SHOW_ALL = "${BASE_URL}$VERSION/car-types/show-all"

    //country
    const val COUNTRY_SHOW_ALL = "${BASE_URL}$VERSION/countries/show-all"

    const val PROVINCE_BY_COUNTRY_ID = "${BASE_URL}$VERSION/provinces/by-country"
    const val CITY_BY_PROVINCE_ID = "${BASE_URL}$VERSION/cities/by-province"

    //delete account
    const val DELETE_ACCOUNT = "${BASE_URL}$VERSION/users/delete"

    //ads
    const val ADS_SHOW_ALL = "${BASE_URL}$VERSION/advertisements/show-all"

    //notifications

    const val NOTIFICATIONS_SHOW_ALL = "${BASE_URL}$VERSION/notifications/show-all"
    const val NOTIFICATIONS_MARK_AS_READ = "${BASE_URL}$VERSION/notifications/mark-all-read"
    const val NOTIFICATIONS_MY_UNREAD = "${BASE_URL}$VERSION/notifications/my/unread"
    const val NOTIFICATIONS_MY_UNREAD_COUNT = "${BASE_URL}$VERSION/notifications/my/unread-count"

    //search
    const val SEARCH = "${BASE_URL}$VERSION/search"

    //provider-request
    const val PROVIDER_REQUEST = "${BASE_URL}$VERSION/provider-requests/show-all"

}