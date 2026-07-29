package com.wadii.pages.shared.profile

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.core.readBytes
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.domain.usecase.UserUseCase
import com.wadii.state.AppState
import com.wadii.utils.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.w3c.files.File

class ProfileViewModel(
    private val userUseCase: UserUseCase,
    private val providerUseCase: ProviderUseCase
) : BaseViewModel<ProfileState, ProfileEvent>() {

    override val initialState: ProfileState get() = ProfileState()

    override val state: StateFlow<ProfileState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.UploadAvatar -> uploadAvatar(event.file)
            is ProfileEvent.UploadBackground -> uploadBackground(event.file)
            is ProfileEvent.SwitchRole -> switchRole()
        }
    }

    private fun load() = screenModelScope.launch {
        userUseCase.userMe { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            user = data.data,
                            isLoading = false,
                        )
                    }
                    AppState.saveUser(
                        user = data.data,
                        token = data.data.token
                    )
                },
                onError = { e, _ ->
                    updateState {
                        it.copy(
                            error = e,
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    private fun uploadAvatar(file: File) = screenModelScope.launch {
        userUseCase.updateImageUser(file.readBytes()) { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(uploadingAvatar = true) }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            user = data.data,
                            uploadingAvatar = false
                        )
                    }
                    AppState.saveUser(data.data, AppState.token ?: "")
                    AppState.toast("Avatar updated!")
                }, onError = { error, code ->
                    updateState { it.copy(uploadingAvatar = false) }
                    AppState.toast("Upload failed", true)
                }
            )
        }
    }

    private fun uploadBackground(file: File) = screenModelScope.launch {
        userUseCase.updateImageBackgroundUser(file.readBytes()) { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(uploadingBg = true) }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            user = data.data,
                            uploadingBg = false
                        )
                    }
                    AppState.saveUser(data.data, AppState.token ?: "")
                    AppState.toast("Background updated!")
                }, onError = { error, code ->
                    updateState { it.copy(uploadingBg = false) }
                    AppState.toast("Upload failed", true)
                }
            )
        }
    }

    private fun switchRole() = screenModelScope.launch {
        val user = _state.value.user ?: return@launch
        val hasProviderProfile = user.provider != null && user.provider.id != 0L
        updateState { it.copy(switchingRole = true) }
        val onResult: (RequestState<BaseResponse<User>>) -> Unit = { r ->
            r.handelState(
                onSuccess = { data ->
                    val updated = data.data
                    if (updated != null) {
                        AppState.saveUser(updated, AppState.token ?: "")
                        updateState {
                            it.copy(
                                user = updated,
                                switchingRole = false,
                            )
                        }
                        AppState.toast(if (updated.role == "PROVIDER") "Switched to Seller mode" else "Switched to Buyer mode")
                        load()
                    } else {
                        updateState { it.copy(switchingRole = false) }
                    }
                },
                onError = { e, _ ->
                    updateState { it.copy(switchingRole = false) }
                    AppState.toast("Failed to switch role", true)
                }
            )
        }

        if (user.role == "PROVIDER") {
            providerUseCase.putItUser(user.id, onResult)
        } else if (hasProviderProfile) {
            providerUseCase.putItProvider(user.id, onResult)
        } else {
            updateState { it.copy(switchingRole = false) }
        }
    }
}
