package com.teacheronline.screens.auth.login

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.data.firebase.FcmService
import com.teacheronline.domain.model.auth.AuthRequest
import com.teacheronline.domain.usecase.AuthUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
            LoginEvent.Submit -> submit()
        }
    }

    private fun submit() = screenModelScope.launch {
        val current = _state.value
        if (current.loading) return@launch
        updateState { it.copy(loading = true) }
        val fcmToken = FcmService.fetchToken() ?: ""
        authUseCase.login(
            AuthRequest(email = current.email, password = current.password, fcmToken = fcmToken)
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
