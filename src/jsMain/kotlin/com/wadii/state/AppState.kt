package com.wadii.state

import androidx.compose.runtime.*
import com.wadii.model.User
import com.wadii.router.Route
import kotlinx.browser.localStorage

object AppState {
    var user by mutableStateOf<User?>(null)
    var token by mutableStateOf<String?>(null)
    var route by mutableStateOf(Route.LOGIN)
    var routeParam by mutableStateOf<String?>(null)

    // Toast notifications
    var toastMessage by mutableStateOf<String?>(null)
    var toastIsError by mutableStateOf(false)

    init {
        val storedToken = localStorage.getItem("token")
        val storedUser = localStorage.getItem("user")
        if (storedToken != null && storedUser != null) {
            token = storedToken
            try {
                val decoded = kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                }.decodeFromString(User.serializer(), storedUser)
                user = decoded
                route = when (decoded.role) {
                    "ADMIN" -> Route.ADMIN_DASHBOARD
                    "PROVIDER" -> Route.PROVIDER_DASHBOARD
                    else -> Route.HOME
                }
            } catch (_: Exception) {
                clearStorage()
            }
        }
    }

    fun login(u: User, t: String) {
        user = u
        token = t
        localStorage.setItem("token", t)
        localStorage.setItem("user", kotlinx.serialization.json.Json.encodeToString(User.serializer(), u))
        route = when (u.role) {
            "ADMIN" -> Route.ADMIN_DASHBOARD
            "PROVIDER" -> Route.PROVIDER_DASHBOARD
            else -> Route.HOME
        }
    }

    fun logout() {
        user = null
        token = null
        clearStorage()
        route = Route.LOGIN
        routeParam = null
    }

    fun navigate(r: Route, param: String? = null) {
        route = r
        routeParam = param
    }

    fun toast(msg: String, isError: Boolean = false) {
        toastMessage = msg
        toastIsError = isError
    }

    fun clearToast() { toastMessage = null }

    private fun clearStorage() {
        localStorage.removeItem("token")
        localStorage.removeItem("user")
    }
}
