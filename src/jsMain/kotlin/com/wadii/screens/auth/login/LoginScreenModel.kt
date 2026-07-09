package com.wadii.screens.auth.login

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.firebase.FcmService
import com.wadii.domain.model.auth.login.LoginRequest
import com.wadii.domain.usecase.AuthUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.w3c.notifications.Notification.Companion.permission

class LoginViewModel(private val authUseCase: AuthUseCase) :
    BaseViewModel<LoginState, LoginEvent>() {

    override val initialState: LoginState
        get() = LoginState()

    override val state: StateFlow<LoginState> = _state
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is LoginEvent.SetPassword -> updateState { it.copy(password = event.value) }
            LoginEvent.Submit -> {
              submit()
            }
        }
    }

    private fun submit() = screenModelScope.launch {
        val current = _state.value
        if (current.loading) return@launch
        updateState { it.copy(loading = true) }
        console.log("Permission:", permission)
        val fcmToken = FcmService.fetchToken().orEmpty()
        console.log("FCM token:", fcmToken)
        authUseCase.login(
            LoginRequest(
                email = current.email,
                password = current.password,
                fcmToken = fcmToken
            )
        ) { response ->
            response.handelState(
                onLoading = {},
                onSuccess = { data ->
                    val user = data.data
                    if (user != null && user.token != null) {
                        AppState.saveUser(user, user.token!!)
                        AppState.toast("Welcome back, ${user.firstName}!")
                    } else {
                        AppState.toast("Invalid email or password", true)
                    }
                    updateState { it.copy(loading = false) }
                },
                onError = { _, _ ->
                    AppState.toast("Invalid email or password", true)
                    updateState { it.copy(loading = false) }
                }
            )
        }
    }
}
