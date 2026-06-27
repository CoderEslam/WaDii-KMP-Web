package com.wadii.state

import androidx.compose.runtime.*
import com.wadii.domain.model.auth.login.User
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

object AppState {
    var user by mutableStateOf<User?>(null)
    var token by mutableStateOf<String?>(null)

    var toastMessage by mutableStateOf<String?>(null)
    var toastIsError by mutableStateOf(false)

    var darkMode by mutableStateOf(localStorage.getItem("darkMode") == "true")

    fun toggleDarkMode() {
        darkMode = !darkMode
        localStorage.setItem("darkMode", darkMode.toString())
    }

    init {
        val storedToken = localStorage.getItem("token")
        val storedUser = localStorage.getItem("user")
        if (storedToken != null && storedUser != null) {
            token = storedToken
            try {
                val decoded = Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                }.decodeFromString(User.serializer(), storedUser)
                user = decoded
            } catch (_: Exception) {
                clearStorage()
            }
        }
    }

    fun login(u: User, t: String) {
        user = u
        token = t
        localStorage.setItem("token", t)
        localStorage.setItem(
            "user", Json.encodeToString(
                User.serializer(), u
            )
        )
    }

    fun logout() {
        user = null
        token = null
        clearStorage()
    }

    fun toast(msg: String, isError: Boolean = false) {
        toastMessage = msg
        toastIsError = isError
    }

    fun clearToast() {
        toastMessage = null
    }

    private fun clearStorage() {
        localStorage.removeItem("token")
        localStorage.removeItem("user")
    }
}
