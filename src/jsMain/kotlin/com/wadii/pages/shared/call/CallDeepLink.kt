package com.wadii.pages.shared.call

import org.w3c.dom.url.URLSearchParams

/**
 * Lets a host mobile app (Android/iOS WebView) jump straight into an active call by loading
 * `index.html#call?...` — hash-based so no server-side route/rewrite is needed, since the
 * fragment never reaches the server. See agora-mobile.md for the WebView wrapper side.
 */
data class CallDeepLink(
    val channelName: String,
    val remoteUserId: Int,
    val remoteUserName: String,
    val remoteUserImage: String?,
    val withVideo: Boolean,
    val isCaller: Boolean,
    val myUserId: Int,
    val myUserName: String,
    val token: String
)

private const val PREFIX = "#call?"

fun parseCallDeepLink(hash: String): CallDeepLink? {
    if (!hash.startsWith(PREFIX)) return null
    val params = URLSearchParams(hash.removePrefix(PREFIX))

    val channelName = params.get("channelName")?.takeIf { it.isNotBlank() } ?: return null
    val remoteUserId = params.get("remoteUserId")?.toIntOrNull() ?: return null
    val myUserId = params.get("myUserId")?.toIntOrNull() ?: return null
    val token = params.get("token")?.takeIf { it.isNotBlank() } ?: return null

    return CallDeepLink(
        channelName = channelName,
        remoteUserId = remoteUserId,
        remoteUserName = params.get("remoteUserName") ?: "",
        remoteUserImage = params.get("remoteUserImage"),
        withVideo = params.get("withVideo") == "true",
        isCaller = params.get("isCaller") == "true",
        myUserId = myUserId,
        myUserName = params.get("myUserName") ?: "",
        token = token
    )
}