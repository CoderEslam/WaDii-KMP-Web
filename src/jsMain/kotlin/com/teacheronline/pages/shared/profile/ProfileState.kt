package com.teacheronline.pages.shared.profile

import com.teacheronline.domain.model.auth.login.User

data class ProfileState(
    val user: User = User()
)
