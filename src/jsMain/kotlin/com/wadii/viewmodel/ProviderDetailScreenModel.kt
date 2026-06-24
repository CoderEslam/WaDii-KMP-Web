package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiFollowProvider
import com.wadii.api.apiGetProvider
import com.wadii.api.apiUnfollowProvider
import com.wadii.model.Provider
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class ProviderDetailEvent {
    data class Load(val providerId: Long) : ProviderDetailEvent()
    data class ToggleFollow(val providerId: Long) : ProviderDetailEvent()
}

data class ProviderDetailData(
    val provider: Provider,
    val following: Boolean = false
)

class ProviderDetailScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<ProviderDetailData>>(UiState.Loading)
        private set

    fun onEvent(event: ProviderDetailEvent) = when (event) {
        is ProviderDetailEvent.Load -> load(event.providerId)
        is ProviderDetailEvent.ToggleFollow -> toggleFollow(event.providerId)
    }

    private fun load(id: Long) {
        screenModelScope.launch {
            state = UiState.Loading
            val provider = apiGetProvider(id)
            state = if (provider != null) UiState.Success(ProviderDetailData(provider))
                    else UiState.Error("Provider not found.")
        }
    }

    private fun toggleFollow(id: Long) {
        val d = (state as? UiState.Success)?.data ?: return
        screenModelScope.launch {
            if (d.following) {
                if (apiUnfollowProvider(id)) { mutate { copy(following = false) }; AppState.toast("Unfollowed") }
            } else {
                if (apiFollowProvider(id)) { mutate { copy(following = true) }; AppState.toast("Following!") }
            }
        }
    }

    private fun mutate(block: ProviderDetailData.() -> ProviderDetailData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
