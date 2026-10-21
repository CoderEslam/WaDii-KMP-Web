package com.wadii.utils

import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

fun HttpResponse.getStatusCode(): StatusCode = StatusCode.entries.find {
    it.code == status.value
} ?: StatusCode.Unknown


suspend inline fun <reified Body, reified Response> HttpClient.postApiResponse(
    urlString: String,
    body: Body? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): RequestState<Response> {
    return try {
        val response: HttpResponse = this.post(urlString) {
            block()
            body?.let { setBody(it) }
        }

        if (response.status.value == 200) {
            // Try to parse body safely
            try {
                val parsedBody: Response = response.body()
                RequestState.Success(parsedBody)
            } catch (e: Exception) {
                RequestState.Error(
                    response.parseFailureMessage(e),
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.apiErrorMessage(), response.getStatusCode())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // Catch IOException, Timeout, etc.
        RequestState.Error("Network error: ${e.message}")
    }
}

suspend inline fun <reified Response> HttpClient.postApiResponse(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {},
): RequestState<Response> {
    return try {
        val response: HttpResponse = this.post(urlString) {
            block()
        }
        if (response.status.value == 200) {
            // Try to parse body safely
            try {
                val parsedBody: Response = response.body()
                RequestState.Success(parsedBody)
            } catch (e: Exception) {
                RequestState.Error(
                    response.parseFailureMessage(e),
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.apiErrorMessage(), response.getStatusCode())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // Catch IOException, Timeout, etc.
        RequestState.Error("Network error: ${e.message}")
    }
}

suspend inline fun <reified Response> HttpClient.getApiResponse(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {},
): RequestState<Response> {
    return try {
        val response: HttpResponse = this.get(urlString) {
            block()
        }

        if (response.status.value == 200) {
            try {
                val parsedBody: Response = response.body()
                RequestState.Success(parsedBody)
            } catch (e: Exception) {
                RequestState.Error(
                    response.parseFailureMessage(e),
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.apiErrorMessage(), response.getStatusCode())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        RequestState.Error("Network error: ${e.message}")
    }
}

suspend inline fun <reified Response> HttpClient.deleteApiResponse(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {},
): RequestState<Response> {
    return try {
        val response: HttpResponse = this.delete(urlString) {
            block()
        }

        if (response.status.value == 200) {
            try {
                val parsedBody: Response = response.body()
                RequestState.Success(parsedBody)
            } catch (e: Exception) {
                RequestState.Error(
                    response.parseFailureMessage(e),
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.apiErrorMessage(), response.getStatusCode())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        RequestState.Error("Network error: ${e.message}")
    }
}

suspend inline fun <reified Body, reified Response> HttpClient.putApiResponse(
    urlString: String,
    body: Body? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): RequestState<Response> {
    return try {
        val response: HttpResponse = this.put(urlString) {
            body?.let { setBody(it) }
            block()
        }

        if (response.status.value == 200) {
            try {
                val parsedBody: Response = response.body()
                RequestState.Success(parsedBody)
            } catch (e: Exception) {
                RequestState.Error(
                    response.parseFailureMessage(e),
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.apiErrorMessage(), response.getStatusCode())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        RequestState.Error("Network error: ${e.message}")
    }
}
/**
 * The backend wraps every result in `{ data, message, statusCode }`, except auth failures which are
 * `{ "error": "..." }`. Users should read the sentence, not the JSON.
 */
private val messageField = Regex("\"(?:message|error)\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")

fun extractServerMessage(raw: String): String? =
    messageField.find(raw)?.groupValues?.get(1)
        ?.replace("\\\"", "\"")?.replace("\\n", "\n")?.trim()
        ?.takeIf { it.isNotEmpty() }

suspend fun HttpResponse.apiErrorMessage(): String {
    val raw = try { bodyAsText() } catch (e: Exception) { "" }
    return extractServerMessage(raw)
        ?: when (status.value) {
            401 -> "Your session has expired. Please sign in again."
            403 -> "You don't have permission to do that."
            404 -> "We couldn't find what you asked for."
            in 500..599 -> "The server had a problem. Please try again in a moment."
            else -> raw.take(200).ifBlank { "Something went wrong (code ${status.value})." }
        }
}

/**
 * Several endpoints answer HTTP 200 with `data: null` and the reason in `message` (for example a failed
 * role check). Our typed models can't hold a null `data`, so surface that message instead of a parse error.
 */
suspend fun HttpResponse.parseFailureMessage(cause: Exception): String {
    val raw = try { bodyAsText() } catch (e: Exception) { "" }
    return extractServerMessage(raw) ?: "We couldn't read the server's answer (${cause.message ?: "unknown error"})."
}
