package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiDeleteAd
import com.wadii.api.apiGetAllAds
import com.wadii.api.apiInsertAd
import com.wadii.api.apiUpdateAd
import com.wadii.model.Advertisement
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class AdsEvent {
    object Load : AdsEvent()
    data class ShowModal(val ad: Advertisement?) : AdsEvent()
    object CloseModal : AdsEvent()
    data class Delete(val adId: Long) : AdsEvent()
    data class Save(val ad: Advertisement?, val fields: Map<String, Any?>) : AdsEvent()
}

data class AdsData(
    val ads: List<Advertisement> = emptyList(),
    val showModal: Boolean = false,
    val editAd: Advertisement? = null,
    val saving: Boolean = false
)

class AdsScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<AdsData>>(UiState.Loading)
        private set

    init { onEvent(AdsEvent.Load) }

    fun onEvent(event: AdsEvent) = when (event) {
        AdsEvent.Load -> load()
        is AdsEvent.ShowModal -> mutate { copy(showModal = true, editAd = event.ad) }
        AdsEvent.CloseModal -> mutate { copy(showModal = false, editAd = null) }
        is AdsEvent.Delete -> delete(event.adId)
        is AdsEvent.Save -> save(event.ad, event.fields)
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(AdsData(ads = apiGetAllAds()))
        }
    }

    private fun delete(id: Long) {
        screenModelScope.launch {
            if (apiDeleteAd(id)) { AppState.toast("Deleted"); load() }
            else AppState.toast("Failed to delete", true)
        }
    }

    private fun save(ad: Advertisement?, fields: Map<String, Any?>) {
        mutate { copy(saving = true) }
        screenModelScope.launch {
            val result = if (ad != null) apiUpdateAd(fields) else apiInsertAd(fields)
            mutate { copy(saving = false) }
            if (result != null) {
                AppState.toast(if (ad != null) "Ad updated!" else "Ad created!")
                load()
            } else {
                AppState.toast("Failed to save ad", true)
            }
        }
    }

    private fun mutate(block: AdsData.() -> AdsData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
