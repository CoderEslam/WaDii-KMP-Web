package com.wadii.screens.auth.login

sealed class LoginEvent {
    data class SetEmail(val value: String) : LoginEvent()
    data class SetPassword(val value: String) : LoginEvent()
    object Submit : LoginEvent()
}
