package com.teacheronline.data.firebase

import com.teacheronline.utils.Constants
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlin.js.Promise

/**
 * Requests notification permission and returns this browser's FCM registration
 * token, so it can be sent to the backend (see fcmToken on login/register/update-user).
 * Sending pushes to that token is a server-side concern (Firebase Admin SDK / admin.json)
 * and lives outside this repo.
 */
object FcmService {

    private var app: dynamic = null

    //    const firebaseConfig =
//    {
//        apiKey: "AIzaSyChi41C0aEzzjctisYpqpHvzJKN6uD0v8A",
//        authDomain: "wadii-kmp.firebaseapp.com",
//        projectId: "wadii-kmp",
//        storageBucket: "wadii-kmp.firebasestorage.app",
//        messagingSenderId: "1041244088662",
//        appId: "1:1041244088662:web:292b831dddb6727ed40ef7",
//        measurementId: "G-M4E2S6WX2X"
//    };
    private fun firebaseApp(): dynamic {
        if (app != null) return app
        val created = FirebaseApp.initializeApp(
            firebaseOptions(
                apiKey = Constants.FIREBASE_API_KEY,
                authDomain = Constants.FIREBASE_AUTH_DOMAIN,
                projectId = Constants.FIREBASE_PROJECT_ID,
                storageBucket = Constants.FIREBASE_STORAGE_BUCKET,
                messagingSenderId = Constants.FIREBASE_MESSAGING_SENDER_ID,
                appId = Constants.FIREBASE_APP_ID
            )
        )
        app = created
        return created
    }

    suspend fun fetchToken(): String? = runCatching {
        val notificationCtor = window.asDynamic().Notification ?: return null

        val permission = notificationCtor.requestPermission()
            .unsafeCast<Promise<String>>()
            .await()
        if (permission != "granted") return null

        val serviceWorker = window.navigator.asDynamic().serviceWorker
            ?: return null
        val registration = serviceWorker
            .register("/firebase-messaging-sw.js")
            .unsafeCast<Promise<dynamic>>()
            .await()

        val messaging = FirebaseMessaging.getMessaging(firebaseApp())
        FirebaseMessaging.getToken(
            messaging,
            getTokenOptions(
                Constants.FIREBASE_VAPID_KEY,
                registration
            )
        ).await()
    }.getOrElse { error ->
        console.error("FCM token fetch failed:", error)
        null
    }

    /**
     * Fires for pushes that arrive while this tab is open and focused. Background pushes
     * (tab unfocused/closed) never reach here — those are caught in firebase-messaging-sw.js.
     * Returns an unsubscribe function.
     */
    fun observeForegroundMessages(onReceive: (title: String, body: String) -> Unit): () -> Unit {
        val messaging = FirebaseMessaging.getMessaging(firebaseApp())
        return FirebaseMessaging.onMessage(messaging) { payload ->
            val notification = payload.notification
            if (notification != null) {
                onReceive(notification.title.orEmpty(), notification.body.orEmpty())
            }
        }
    }
}
