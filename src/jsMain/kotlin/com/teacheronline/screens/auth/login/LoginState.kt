package com.teacheronline.screens.auth.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false
)
