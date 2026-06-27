package com.wadii.pages.shared.profile

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiGetMe
import com.wadii.data.api.apiUploadImage
import com.wadii.data.api.apiUploadImageBackground
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch
import org.w3c.files.File

class ProfileScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<ProfileState>>(UiState.Loading)
        private set

    init {
        onEvent(ProfileEvent.Load)
    }

    fun onEvent(event: ProfileEvent) = when (event) {
        ProfileEvent.Load -> load()
        is ProfileEvent.UploadAvatar -> uploadAvatar(event.file)
        is ProfileEvent.UploadBackground -> uploadBackground(event.file)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            val user = AppState.user ?: apiGetMe()
            state = if (user != null) UiState.Success(ProfileState(user))
            else UiState.Error("Unable to load profile.")
        }
    }

    private fun uploadAvatar(file: File) {
        screenModelScope.launch {
            mutate { copy(uploadingAvatar = true) }
            val uploaded = apiUploadImage(file)
            if (uploaded != null) {
                mutate { copy(user = user.copy(image = uploaded), uploadingAvatar = false) }
                AppState.toast("Avatar updated!")
            } else {
                mutate { copy(uploadingAvatar = false) }
                AppState.toast("Upload failed", true)
            }
        }
    }

    private fun uploadBackground(file: File) {
        screenModelScope.launch {
            mutate { copy(uploadingBg = true) }
            val uploaded = apiUploadImageBackground(file)
            if (uploaded != null) {
                mutate { copy(user = user.copy(backgroundImage = uploaded), uploadingBg = false) }
                AppState.toast("Background updated!")
            } else {
                mutate { copy(uploadingBg = false) }
                AppState.toast("Upload failed", true)
            }
        }
    }

    private fun mutate(block: ProfileState.() -> ProfileState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
