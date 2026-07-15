package com.teacheronline.pages.shared.profile

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// There's no /users/me or update-profile endpoint in the API — AppState.user (populated at
// login) is the only source of truth, so this ScreenModel just mirrors it, read-only.
class ProfileViewModel : BaseViewModel<ProfileState, ProfileEvent>() {

    override val initialState: ProfileState get() = ProfileState(user = AppState.user ?: com.teacheronline.domain.model.auth.login.User())

    override val state: StateFlow<ProfileState> = _state
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ProfileEvent) {}
}
