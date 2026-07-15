package com.teacheronline.screens.auth.register

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false
)
