package com.wadii.screens.providerDetail

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiFollowProvider
import com.wadii.data.api.apiGetProvider
import com.wadii.data.api.apiUnfollowProvider
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class ProviderDetailScreenModel : ScreenModel {
    var state by mutableStateOf<UiState<ProviderDetailState>>(UiState.Loading)
        private set

    fun onEvent(event: ProviderDetailEvent) = when (event) {
        is ProviderDetailEvent.Load -> load(event.providerId)
        is ProviderDetailEvent.ToggleFollow -> toggleFollow(event.providerId)
    }

    private fun load(id: Int) {
        screenModelScope.launch {
            state = UiState.Loading
            val provider = apiGetProvider(id)
            state = if (provider != null) UiState.Success(ProviderDetailState(provider))
                    else UiState.Error("Provider not found.")
        }
    }

    private fun toggleFollow(id: Int) {
        val d = (state as? UiState.Success)?.data ?: return
        screenModelScope.launch {
            if (d.following) {
                if (apiUnfollowProvider(id)) { mutate { copy(following = false) }; AppState.toast("Unfollowed") }
            } else {
                if (apiFollowProvider(id)) { mutate { copy(following = true) }; AppState.toast("Following!") }
            }
        }
    }

    private fun mutate(block: ProviderDetailState.() -> ProviderDetailState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
