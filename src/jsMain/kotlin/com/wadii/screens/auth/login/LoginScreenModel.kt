package com.wadii.screens.auth.login

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiLogin
import com.wadii.state.AppState
import kotlinx.coroutines.launch

class LoginScreenModel : ScreenModel {
    var state by mutableStateOf(LoginState())
        private set

    fun onEvent(event: LoginEvent) = when (event) {
        is LoginEvent.SetEmail -> state = state.copy(email = event.value)
        is LoginEvent.SetPassword -> state = state.copy(password = event.value)
        LoginEvent.Submit -> submit()
    }

    private fun submit() {
        if (state.loading) return
        state = state.copy(loading = true)
        screenModelScope.launch {
            val user = apiLogin(state.email, state.password)
            state = state.copy(loading = false)
            if (user != null && user.token != null) {
                AppState.login(user, user.token!!)
                AppState.toast("Welcome back, ${user.firstName}!")
            } else {
                AppState.toast("Invalid email or password", true)
            }
        }
    }
}
