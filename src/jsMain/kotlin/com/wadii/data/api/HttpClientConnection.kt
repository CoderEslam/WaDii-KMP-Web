package com.wadii.data.api

import com.wadii.utils.SettingsManager
import com.wadii.utils.SettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.plugins.logging.LogLevel
//import io.ktor.client.plugins.logging.Logger
//import io.ktor.client.plugins.logging.Logging
//import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

fun createHttpClient(): HttpClient {
    return HttpClient(Js) {
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
        }
//        install(Logging) {
//            level = LogLevel.ALL // Choose the level of detail (BODY, HEADERS, INFO, ALL)
//            logger = Logger.SIMPLE // You can also use Logger.DEFAULT or a custom logger
//        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(DefaultRequest) {
//            header("Authorization", "Bearer ${settingsManager}")
            contentType(ContentType.Application.Json)
        }
    }
}


fun createHttpClientSendFile(): HttpClient {
    return HttpClient(Js) {
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 60_000
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
//        install(Logging) {
//            level = LogLevel.ALL // Choose the level of detail (BODY, HEADERS, INFO, ALL)
//            logger = Logger.SIMPLE // You can also use Logger.DEFAULT or a custom logger
//        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        install(DefaultRequest) {
//            if (settingsManager.getUser().token.isNotNullOrEmptyString()) {
//                header("Authorization", "Bearer ${settingsManager.getUser().token}")
//            }
            contentType(ContentType.Application.Json)
        }
    }
}

fun json(): Json {
    return Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        coerceInputValues = true
    }
}

fun clientWebSocket(): HttpClient {
    return HttpClient() {
//        install(Logging) {
//            level = LogLevel.ALL // Choose the level of detail (BODY, HEADERS, INFO, ALL)
//            logger = Logger.SIMPLE // You can also use Logger.DEFAULT or a custom logger
//        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }
}


object DefaultStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DefaultString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }

    override fun deserialize(decoder: Decoder): String {
        return try {
            decoder.decodeString()
        } catch (e: Exception) {
            "" // fallback if null or invalid
        }
    }
}