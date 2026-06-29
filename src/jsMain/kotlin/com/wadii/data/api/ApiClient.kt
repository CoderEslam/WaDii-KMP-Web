package com.wadii.data.api

import com.wadii.state.AppState
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.math.round

//val json = Json {
//    ignoreUnknownKeys = true
//    isLenient = true
//    coerceInputValues = true
//}
//
//val httpClient = HttpClient(Js) {
//    install(ContentNegotiation) { json(json) }
//    expectSuccess = false
//}
//
fun HttpRequestBuilder.auth() {
    AppState.token?.let { header(HttpHeaders.Authorization, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlc2xhbWdoYXp5OUBleGFtcGxlLmNvbSIsImp0aSI6IjQiLCJpYXQiOjE3ODE4Njk3MjYsImV4cCI6MTc4MzczNTk2Nn0.Xk60Srlnd4tw2f6CS2HQmflCVaYxFpIB2OYmlul5Kog") }
}
//
//suspend fun HttpResponse.checkAuth() {
//    if (status == HttpStatusCode.Unauthorized) {
//        AppState.logout()
//    }
//}

fun Double.to1dp(): String {
    val rounded = round(this * 10).toInt()
    return "${rounded / 10}.${rounded % 10}"
}
