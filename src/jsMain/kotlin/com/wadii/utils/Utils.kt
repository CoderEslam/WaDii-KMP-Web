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
                    "Failed to parse response: ${e.message}",
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.bodyAsText(), response.getStatusCode())
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
                    "Failed to parse response: ${e.message}",
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.bodyAsText(), response.getStatusCode())
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
                    "Failed to parse response: ${e.message}",
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.bodyAsText(), response.getStatusCode())
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
                    "Failed to parse response: ${e.message}",
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.bodyAsText(), response.getStatusCode())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        RequestState.Error("Network error: ${e.message}")
    }
}

suspend inline fun <reified Body, reified Response> HttpClient.putApiResponse(
    urlString: String,
    body: Body,
    block: HttpRequestBuilder.() -> Unit = {},
): RequestState<Response> {
    return try {
        val response: HttpResponse = this.put(urlString) {
            setBody(body)
            block()
        }

        if (response.status.value == 200) {
            try {
                val parsedBody: Response = response.body()
                RequestState.Success(parsedBody)
            } catch (e: Exception) {
                RequestState.Error(
                    "Failed to parse response: ${e.message}",
                    response.getStatusCode()
                )
            }
        } else {
            RequestState.Error(response.bodyAsText(), response.getStatusCode())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        RequestState.Error("Network error: ${e.message}")
    }
}