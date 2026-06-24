package com.wadii.api

import com.wadii.state.AppState
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

const val BASE_URL = "http://172.28.0.134:8080"

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

val httpClient = HttpClient(Js) {
    install(ContentNegotiation) { json(json) }
    expectSuccess = false
}

fun HttpRequestBuilder.auth() {
    AppState.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
}

suspend fun HttpResponse.checkAuth() {
    if (status == HttpStatusCode.Unauthorized) {
        AppState.logout()
    }
}

fun Double.to1dp(): String {
    val rounded = kotlin.math.round(this * 10).toInt()
    return "${rounded / 10}.${rounded % 10}"
}
