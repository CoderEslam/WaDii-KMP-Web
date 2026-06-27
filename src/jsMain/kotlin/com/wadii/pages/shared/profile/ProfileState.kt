package com.wadii.pages.shared.profile

import com.wadii.domain.model.auth.login.User


data class ProfileState(
    val user: User,
    val uploadingAvatar: Boolean = false,
    val uploadingBg: Boolean = false
)
