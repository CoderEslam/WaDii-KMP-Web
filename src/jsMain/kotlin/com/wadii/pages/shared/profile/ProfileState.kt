package com.wadii.pages.shared.profile

import com.wadii.domain.model.auth.login.User


data class ProfileState(
    val user: User? = null,
    val uploadingAvatar: Boolean = false,
    val uploadingBg: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
