package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiGetMe
import com.wadii.api.apiUploadImage
import com.wadii.model.User
import com.wadii.state.AppState
import kotlinx.coroutines.launch
import org.w3c.files.File

sealed class ProfileEvent {
    object Load : ProfileEvent()
    data class UploadAvatar(val file: File) : ProfileEvent()
    data class UploadBackground(val file: File) : ProfileEvent()
}

data class ProfileData(
    val user: User,
    val uploadingAvatar: Boolean = false,
    val uploadingBg: Boolean = false
)

class ProfileScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<ProfileData>>(UiState.Loading)
        private set

    init { onEvent(ProfileEvent.Load) }

    fun onEvent(event: ProfileEvent) = when (event) {
        ProfileEvent.Load -> load()
        is ProfileEvent.UploadAvatar -> uploadAvatar(event.file)
        is ProfileEvent.UploadBackground -> uploadBackground(event.file)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            val user = AppState.user ?: apiGetMe()
            state = if (user != null) UiState.Success(ProfileData(user))
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
            val uploaded = apiUploadImage(file)
            if (uploaded != null) {
                mutate { copy(user = user.copy(backgroundImage = uploaded), uploadingBg = false) }
                AppState.toast("Background updated!")
            } else {
                mutate { copy(uploadingBg = false) }
                AppState.toast("Upload failed", true)
            }
        }
    }

    private fun mutate(block: ProfileData.() -> ProfileData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
