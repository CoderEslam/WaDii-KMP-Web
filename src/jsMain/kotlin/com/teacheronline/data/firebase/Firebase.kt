package com.teacheronline.data.firebase

import kotlin.js.Promise

@JsModule("firebase/app")
@JsNonModule
external object FirebaseApp {
    fun initializeApp(options: FirebaseOptions): dynamic
}

external interface FirebaseOptions {
    var apiKey: String
    var authDomain: String
    var projectId: String
    var storageBucket: String
    var messagingSenderId: String
    var appId: String
}

fun firebaseOptions(
    apiKey: String,
    authDomain: String,
    projectId: String,
    storageBucket: String,
    messagingSenderId: String,
    appId: String
): FirebaseOptions = js("({})").unsafeCast<FirebaseOptions>().apply {
    this.apiKey = apiKey
    this.authDomain = authDomain
    this.projectId = projectId
    this.storageBucket = storageBucket
    this.messagingSenderId = messagingSenderId
    this.appId = appId
}

@JsModule("firebase/messaging")
@JsNonModule
external object FirebaseMessaging {
    fun getMessaging(app: dynamic): dynamic
    fun getToken(messaging: dynamic, options: GetTokenOptions): Promise<String>
    fun onMessage(messaging: dynamic, callback: (MessagePayload) -> Unit): () -> Unit
}

external interface MessagePayload {
    val notification: NotificationBody?
    val data: dynamic
}

external interface NotificationBody {
    val title: String?
    val body: String?
    val image: String?
}

external interface GetTokenOptions {
    var vapidKey: String
    var serviceWorkerRegistration: dynamic
}

fun getTokenOptions(
    vapidKey: String,
    registration: dynamic
): GetTokenOptions = js("({})").unsafeCast<GetTokenOptions>().apply {
    this.vapidKey = vapidKey
    this.serviceWorkerRegistration = registration
}
