package com.teacheronline.state

import androidx.compose.runtime.*
import com.teacheronline.core.fromJson
import com.teacheronline.core.toJson
import com.teacheronline.domain.model.auth.login.User
import com.teacheronline.domain.model.call.CallSignal
import kotlinx.browser.localStorage

enum class ToastVariant { Success, Warning, Error }

object AppState {
    // User.equals() compares only by id, so the default structural-equality policy would
    // silently discard writes where the id is unchanged but other fields (name, provider, etc.)
    // were updated — neverEqualPolicy forces every assignment to actually propagate.
    var user by mutableStateOf<User?>(null, neverEqualPolicy())
    var token by mutableStateOf<String?>(null)

    var toastMessage by mutableStateOf<String?>(null)
    var toastVariant by mutableStateOf(ToastVariant.Success)

    var incomingCall by mutableStateOf<CallSignal?>(null)

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

    fun saveUser(user: User, token: String) {
        this.user = user
        this.token = token
        localStorage.setItem("token", token)
        localStorage.setItem("user", user.toJson())
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
