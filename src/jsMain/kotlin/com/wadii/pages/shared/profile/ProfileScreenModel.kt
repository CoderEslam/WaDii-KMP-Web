package com.wadii.pages.shared.profile

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.api.apiUploadImage
import com.wadii.data.api.apiUploadImageBackground
import com.wadii.domain.usecase.UserUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.w3c.files.File

class ProfileViewModel(
    private val userUseCase: UserUseCase
) : BaseViewModel<ProfileState, ProfileEvent>() {

    override val initialState: ProfileState get() = ProfileState()

    override val state: StateFlow<ProfileState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.Load -> load()
            is ProfileEvent.UploadAvatar -> uploadAvatar(event.file)
            is ProfileEvent.UploadBackground -> uploadBackground(event.file)
        }
    }

    private fun load() = screenModelScope.launch {
        val cached = AppState.user
        if (cached != null) {
            updateState { it.copy(user = cached, isLoading = false) }
            return@launch
        }
        userUseCase.userMe { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(user = data.data, isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun uploadAvatar(file: File) = screenModelScope.launch {
        updateState { it.copy(uploadingAvatar = true) }
        val uploaded = apiUploadImage(file)
        if (uploaded != null) {
            updateState { it.copy(user = it.user?.copy(image = uploaded), uploadingAvatar = false) }
            AppState.toast("Avatar updated!")
        } else {
            updateState { it.copy(uploadingAvatar = false) }
            AppState.toast("Upload failed", true)
        }
    }

    private fun uploadBackground(file: File) = screenModelScope.launch {
        updateState { it.copy(uploadingBg = true) }
        val uploaded = apiUploadImageBackground(file)
        if (uploaded != null) {
            updateState { it.copy(user = it.user?.copy(backgroundImage = uploaded), uploadingBg = false) }
            AppState.toast("Background updated!")
        } else {
            updateState { it.copy(uploadingBg = false) }
            AppState.toast("Upload failed", true)
        }
    }
}
