package com.wadii.utils

import com.wadii.state.AppState
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.dom.url.URL
import org.w3c.fetch.RequestInit

/**
 * Loads an image that needs the Bearer token (the provider ID and tax documents are private, so a plain
 * `<img src>` would get 401). Returns an object URL to use as the image source, or null when the server refuses.
 */
suspend fun fetchProtectedImage(url: String): String? = try {
    val headers = js("({})")
    headers["Authorization"] = "Bearer ${AppState.token}"
    val response = window.fetch(url, RequestInit(headers = headers)).await()
    if (response.ok) URL.createObjectURL(response.blob().await()) else null
} catch (e: Throwable) {
    null
}
