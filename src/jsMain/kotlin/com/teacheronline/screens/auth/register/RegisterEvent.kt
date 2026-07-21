package com.teacheronline.screens.auth.register

sealed class RegisterEvent {
    data class SetFirstName(val value: String) : RegisterEvent()
    data class SetLastName(val value: String) : RegisterEvent()
    data class SetEmail(val value: String) : RegisterEvent()
    data class SetPassword(val value: String) : RegisterEvent()
    object Submit : RegisterEvent()
}
