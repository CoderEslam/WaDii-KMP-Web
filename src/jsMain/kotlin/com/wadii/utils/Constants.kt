package com.wadii.utils

object Constants {

    const val TOKEN_KEY = "TOKEN_KEY"
    const val DEVELOPMENT_MODE = true
    const val APP_LANGUAGE = "APP_LANGUAGE"
    const val USER_KEY = "USER_KEY"
    const val THEME_KEY = "THEME_KEY"
    const val PORT = ""
    const val IP = "srv1881459.hstgr.cloud"
    const val BASE_URL = "https://$IP$PORT"
    const val WS_URL = "wss://$IP$PORT/web-socket"
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

    fun PROVIDER_REQUEST_PUT_IT_PROVIDER(id: Long) =
        "${BASE_URL}$VERSION/provider-requests/put-it-provider/$id"

    fun PROVIDER_REQUEST_PUT_IT_USER(id: Long) =
        "${BASE_URL}$VERSION/provider-requests/put-it-user/$id"

    //service
    const val SERVICE_LIST = "${BASE_URL}$VERSION/services/show-all"
    const val SERVICE_INSERT = "${BASE_URL}$VERSION/services/insert"
    fun SERVICE_DELETE(id: Long) = "${BASE_URL}$VERSION/services/delete/$id"
    const val SERVICE_UPDATE = "${BASE_URL}$VERSION/services/insert"

    fun UPDATE_PROVIDER(providerId: Long) = "${BASE_URL}$VERSION/providers/update-all/$providerId"
    const val CREATE_PROVIDER_BY_ADMIN = "${BASE_URL}$VERSION/providers/create-by-admin"

    //specialties
    const val SPECIALTIES_LIST = "${BASE_URL}$VERSION/specialties/list"

    //provinces
    const val PROVINCES_LIST = "${BASE_URL}$VERSION/provinces/list"

    //response
    const val RESPONSE_SHOW_ALL = "${BASE_URL}$VERSION/responses/show-all"
    const val RESPONSE_SHOW_ALL_RESPONSES_OF_USER =
        "${BASE_URL}$VERSION/responses/get-all-response-of-user"
    const val ACCEPT_RESPONSE = "${BASE_URL}$VERSION/responses/accept-response"
    const val REJECT_RESPONSE = "${BASE_URL}$VERSION/responses/cancel-response"
    const val CREATE_RESPONSE = "${BASE_URL}$VERSION/responses/insert"


    //order
    const val ORDER_SHOW_ALL = "${BASE_URL}$VERSION/orders/show-all"
    const val ORDER_SHOW_ALL_ORDER_OF_USER = "${BASE_URL}$VERSION/orders/show-all-order-of-user"
    const val ORDER_INSERT = "${BASE_URL}$VERSION/orders/insert"
    const val ORDER_CANCEL = "${BASE_URL}$VERSION/orders/cancel"
    const val SHOW_ALL_ORDER_OF_PROVIDER = "${BASE_URL}$VERSION/orders/show-all-order-of-provider"

    //cancel reasons
    const val ORDER_CANCEL_REASONS_LIST = "${BASE_URL}$VERSION/reasons/show-all"
    const val REASON_INSERT = "${BASE_URL}$VERSION/reasons/insert"
    const val REASON_UPDATE = "${BASE_URL}$VERSION/reasons/insert"
    fun REASON_DELETE(id: Int) = "${BASE_URL}$VERSION/reasons/delete/$id"

    //offers
    const val OFFERS_SHOW_ALL = "${BASE_URL}$VERSION/offers/show-all"
    const val OFFERS_SHOW_ALL_BY_SERVICE_ID = "${BASE_URL}$VERSION/offers/filter-by-service"
    const val INSERT_OFFER = "${BASE_URL}$VERSION/saved-offers/insert"
    const val REMOVE_SAVED_OFFER = "${BASE_URL}$VERSION/saved-offers/remove"
    const val MY_SAVED_OFFERS = "${BASE_URL}$VERSION/saved-offers/my-saved-offers"
    const val CREATE_OFFER = "${BASE_URL}$VERSION/offers/insert"
    const val UPDATE_OFFER = "${BASE_URL}$VERSION/offers/update"
    const val DELETE_OFFER = "${BASE_URL}$VERSION/offers/delete"


    //message
    const val CHAT_LIST = "${BASE_URL}$VERSION/messages/chat-list"
    const val CHAT_SEND_MESSAGE = "${BASE_URL}$VERSION/messages/insert"
    const val SHOW_ALL_MESSAGES = "${BASE_URL}$VERSION/messages/show-all"

    const val CONVERSATION = "${BASE_URL}$VERSION/messages/conversation"

    //livekit
    const val LIVEKIT_TOKEN = "${BASE_URL}$VERSION/livekit/token"

    //car type
    const val CAR_TYPE_SHOW_ALL = "${BASE_URL}$VERSION/car-types/show-all"

    //country
    const val COUNTRY_SHOW_ALL = "${BASE_URL}$VERSION/countries/show-all"
    const val COUNTRY_INSERT = "${BASE_URL}$VERSION/countries/insert"
    const val COUNTRY_UPDATE = "${BASE_URL}$VERSION/countries/update"
    fun COUNTRY_DELETE(id: Long) = "${BASE_URL}$VERSION/countries/delete/$id"

    //province
    const val PROVINCE_SHOW_ALL = "${BASE_URL}$VERSION/provinces/show-all"
    const val PROVINCE_INSERT = "${BASE_URL}$VERSION/provinces/insert"
    const val PROVINCE_UPDATE = "${BASE_URL}$VERSION/provinces/update"
    fun PROVINCE_DELETE(id: Long) = "${BASE_URL}$VERSION/provinces/delete/$id"
    const val PROVINCE_BY_COUNTRY_ID = "${BASE_URL}$VERSION/provinces/by-country"

    //city
    const val CITY_SHOW_ALL = "${BASE_URL}$VERSION/cities/show-all"
    const val CITY_INSERT = "${BASE_URL}$VERSION/cities/insert"
    const val CITY_UPDATE = "${BASE_URL}$VERSION/cities/update"
    fun CITY_DELETE(id: Long) = "${BASE_URL}$VERSION/cities/delete/$id"
    const val CITY_BY_PROVINCE_ID = "${BASE_URL}$VERSION/cities/by-province"

    //delete account
    const val DELETE_ACCOUNT = "${BASE_URL}$VERSION/users/delete"

    //ads
    const val ADS_SHOW_ALL = "${BASE_URL}$VERSION/advertisements/show-all"
    const val ADS_INSERT = "${BASE_URL}$VERSION/advertisements/insert"
    const val ADS_UPDATE = "${BASE_URL}$VERSION/advertisements/update"
    fun ADS_DELETE(id: Long) = "${BASE_URL}$VERSION/advertisements/delete/$id"
    fun TRACK_CLICK(id: Long) = "${BASE_URL}$VERSION/advertisements/track-click/$id"

    //notifications

    const val NOTIFICATIONS_SHOW_ALL = "${BASE_URL}$VERSION/notifications/show-all"
    const val NOTIFICATIONS_MARK_AS_READ = "${BASE_URL}$VERSION/notifications/mark-all-read"
    const val NOTIFICATIONS_MY_UNREAD = "${BASE_URL}$VERSION/notifications/my/unread"
    const val NOTIFICATIONS_MY_UNREAD_COUNT = "${BASE_URL}$VERSION/notifications/my/unread-count"

    //search
    const val SEARCH = "${BASE_URL}$VERSION/search"

    //provider-request
    const val PROVIDER_REQUEST = "${BASE_URL}$VERSION/provider-requests/show-all"
    fun PROVIDER_REQUEST_ACCEPT(id: Long) = "${BASE_URL}$VERSION/provider-requests/accept/$id"
    fun PROVIDER_REQUEST_REJECT(id: Long) = "${BASE_URL}$VERSION/provider-requests/reject/$id"

    fun ORDER_SHOW_BY_ID(id: Int) = "${BASE_URL}$VERSION/orders/show/$id"

    //firebase (web config — public, safe to ship client-side; NOT the admin.json service account)
    //project_id came from wadii.json; the rest isn't in that file at all — it's not a
    //service-account field. Get it from Firebase Console > Project settings > General >
    //Your apps > SDK setup and configuration, and the VAPID key from Cloud Messaging >
    //Web configuration > Web Push certificates.
    const val FIREBASE_API_KEY = "AIzaSyChi41C0aEzzjctisYpqpHvzJKN6uD0v8A"
    const val FIREBASE_AUTH_DOMAIN = "wadii-kmp.firebaseapp.com"
    const val FIREBASE_PROJECT_ID = "wadii-kmp"
    const val FIREBASE_STORAGE_BUCKET = "wadii-kmp.firebasestorage.app"
    const val FIREBASE_MESSAGING_SENDER_ID = "1041244088662"
    const val FIREBASE_APP_ID = "1:1041244088662:web:292b831dddb6727ed40ef7"
    const val FIREBASE_VAPID_KEY =
        "BCZXCUTLg2DQq9WT9AL6AZxTGq37_s8c8s8gFQSIIdojIPahkUaeXcBBrhk4252YCa5Rg0yyOS9jBn7MHO4RAHE"

}