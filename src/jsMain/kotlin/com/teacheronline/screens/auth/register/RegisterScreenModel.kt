package com.teacheronline.screens.auth.register

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

// Register always creates an ADMIN account server-side (doc §2 — role field is ignored), so
// this is the "owner registers their educational center" flow. Teacher/Student/Secretary
// accounts are created afterward by the owner through the admin console.
class RegisterViewModel(
    private val authUseCase: AuthUseCase
) : BaseViewModel<RegisterState, RegisterEvent>() {

    override val initialState: RegisterState get() = RegisterState()

    override val state: StateFlow<RegisterState> = _state
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is RegisterEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is RegisterEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is RegisterEvent.SetPassword -> updateState { it.copy(password = event.value) }
            RegisterEvent.Submit -> submit()
        }
    }

    private fun submit() = screenModelScope.launch {
        val s = _state.value
        if (s.loading) return@launch
        updateState { it.copy(loading = true) }
        val fcmToken = FcmService.fetchToken() ?: ""
        authUseCase.register(
            AuthRequest(email = s.email, password = s.password, fcmToken = fcmToken, firstName = s.firstName, lastName = s.lastName)
        ) { response ->
            response.handelState(
                onLoading = {},
                onSuccess = { data ->
                    val user = data.data
                    if (user != null && user.token != null) {
                        AppState.saveUser(user, user.token!!)
                        AppState.toast("Account created!")
                    } else {
                        AppState.toast("Registration failed.", true)
                    }
                    updateState { it.copy(loading = false) }
                },
                onError = { _, _ ->
                    AppState.toast("Registration failed. Email may already be in use.", true)
                    updateState { it.copy(loading = false) }
                }
            )
        }
    }
}
