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

    override val initialState: AdsState
        get() = AdsState()

    override val state: StateFlow<AdsState> = _state.onStart {
        ads()
    }.stateIn(
        screenModelScope,
        SharingStarted.Companion.WhileSubscribed(5000),
        AdsState()
    )

    override fun onEvent(event: AdsEvent) = when (event) {
        is AdsEvent.ShowModal -> {

        }

        is AdsEvent.CloseModal -> {

        }

        is AdsEvent.Delete -> delete(event.adId)
    }


    private fun ads() = screenModelScope.launch {
        adsUseCase.ads { response ->
            response.handelState(
                onLoading = {

                }, onSuccess = {

                }, onError = { error, code ->

                }
            )
        }
    }

    private fun delete(id: Long) {
        screenModelScope.launch {
            if (apiDeleteAd(id)) {
                AppState.toast("Deleted")
            } else
                AppState.toast("Failed to delete", true)
        }
    }

//    private fun save(ad: Ads?, fields: Map<String, Any?>) {
//        screenModelScope.launch {
//            val result = if (ad != null) apiUpdateAd(fields) else apiInsertAd(fields)
//            mutate { copy(saving = false) }
//            if (result != null) {
//                AppState.toast(if (ad != null) "Ad updated!" else "Ad created!")
//                load()
//            } else {
//                AppState.toast("Failed to save ad", true)
//            }
//        }
//    }
}