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


fun Double.to1dp(): String {
    val rounded = round(this * 10).toInt()
    return "${rounded / 10}.${rounded % 10}"
}
