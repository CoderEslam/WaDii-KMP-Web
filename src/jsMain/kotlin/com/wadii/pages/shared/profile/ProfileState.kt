package com.wadii.pages.shared.profile

import com.wadii.domain.model.auth.login.User


data class ProfileState(
    val user: User? = null,
    val uploadingAvatar: Boolean = false,
    val uploadingBg: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    // User.equals() compares only by id, so a re-load carrying updated fields for the same
    // user id would otherwise be treated as an equal, conflated state by MutableStateFlow and
    // never reach collectors. Bumping this on every load forces the emission through.
    val revision: Int = 0
)
