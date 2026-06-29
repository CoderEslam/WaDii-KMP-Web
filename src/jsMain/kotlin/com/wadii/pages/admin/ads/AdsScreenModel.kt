package com.wadii.pages.admin.ads

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.api.apiDeleteAd
import com.wadii.domain.usecase.AdsUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdsScreenModel(
    private val adsUseCase: AdsUseCase
) : BaseViewModel<AdsState, AdsEvent>() {

    override val initialState: AdsState get() = AdsState()

    override val state: StateFlow<AdsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: AdsEvent) {
        when (event) {
            is AdsEvent.ShowModal -> Unit
            is AdsEvent.CloseModal -> Unit
            is AdsEvent.Delete -> delete(event.adId)
        }
    }

    private fun load() = screenModelScope.launch {
        adsUseCase.ads { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(ads = data.data ?: emptyList(), isLoading = false) } },
                onError = { _, _ -> updateState { it.copy(isLoading = false) } }
            )
        }
    }

    private fun delete(id: Long) = screenModelScope.launch {
        if (apiDeleteAd(id)) { AppState.toast("Deleted"); load() }
        else AppState.toast("Failed to delete", true)
    }
}
