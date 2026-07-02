package com.wadii.state

import androidx.compose.runtime.*
import com.wadii.core.fromJson
import com.wadii.core.toJson
import com.wadii.domain.model.auth.login.User
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

enum class ToastVariant { Success, Warning, Error }

object AppState {
    var user by mutableStateOf<User?>(null)
    var token by mutableStateOf<String?>(null)

    var toastMessage by mutableStateOf<String?>(null)
    var toastVariant by mutableStateOf(ToastVariant.Success)

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
                user = storedUser.fromJson<User>()
            } catch (_: Exception) {
                clearStorage()
            }
        }
    }

    fun login(u: User, t: String) {
        user = u
        token = t
        localStorage.setItem("token", t)
        localStorage.setItem("user", u.toJson())
    }

    fun logout() {
        user = null
        token = null
        clearStorage()
    }

    fun toast(msg: String, isError: Boolean = false) {
        toast(msg, if (isError) ToastVariant.Error else ToastVariant.Success)
    }

    fun toast(msg: String, variant: ToastVariant) {
        toastMessage = msg
        toastVariant = variant
    }

    fun clearToast() {
        toastMessage = null
    }

    private fun clearStorage() {
        localStorage.removeItem("token")
        localStorage.removeItem("user")
    }
}
