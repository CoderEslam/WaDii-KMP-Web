package com.wadii.pages.admin.ads

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.ads.InsertAds
import com.wadii.domain.usecase.AdsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdsScreenModel(
    private val existingAd: Ads?,
    private val adsUseCase: AdsUseCase
) : BaseViewModel<AdsState, AdsEvent>() {

    override val initialState: AdsState get() = AdsState()

    override val state: StateFlow<AdsState> = _state
        .onStart {
            existingAd?.let { ad ->
                updateState {
                    it.copy(
                        id = ad.id,
                        title = ad.title,
                        description = ad.description,
                        advertiserName = ad.advertiserName,
                        imageUrl = ad.imageUrl,
                        targetUrl = ad.targetUrl,
                        startDate = ad.startDate.take(10),
                        endDate = ad.endDate.take(10),
                        priority = ad.priority.toString()
                    )
                }
            }
            ads()
        }
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    override fun onEvent(event: AdsEvent) {
        when (event) {
            is AdsEvent.Delete -> delete(event.adId)
            is AdsEvent.SetTitle -> updateState { it.copy(title = event.value) }
            is AdsEvent.SetDescription -> updateState { it.copy(description = event.value) }
            is AdsEvent.SetAdvertiserName -> updateState { it.copy(advertiserName = event.value) }
            is AdsEvent.SetImageUrl -> updateState { it.copy(imageUrl = event.value) }
            is AdsEvent.SetTargetUrl -> updateState { it.copy(targetUrl = event.value) }
            is AdsEvent.SetStartDate -> updateState { it.copy(startDate = event.value) }
            is AdsEvent.SetEndDate -> updateState { it.copy(endDate = event.value) }
            is AdsEvent.SetPriority -> updateState { it.copy(priority = event.value) }
            is AdsEvent.Submit -> submit()
        }
    }

    private fun ads() = screenModelScope.launch {
        adsUseCase.ads { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            ads = data.data,
                            isLoading = false
                        )
                    }
                },
                onError = { _, _ -> updateState { it.copy(isLoading = false) } }
            )
        }
    }

    private fun delete(id: Long) = screenModelScope.launch {
        adsUseCase.deleteAds(id) { response ->
            response.handelState(
                onLoading = {
                    updateState {
                        it.copy(isLoading = true)
                    }
                }, onSuccess = {
                    updateState {
                        it.copy(
                            ads = it.ads.filter { ad -> ad.id != id },
                            isLoading = false
                        )
                    }
                    ads()
                }, onError = { error, code ->
                    updateState {
                        it.copy(isLoading = false)
                    }
                }
            )
        }
    }

    private fun submit() = screenModelScope.launch {
        if (_state.value.isEdit) {
            updateAds()
        } else {
            insertAds()
        }
    }

    private fun insertAds() = screenModelScope.launch {
        val startDate = _state.value.startDate.split("-")
        val endDate = _state.value.endDate.split("-")
        val st = "${startDate[0]}-${startDate[1]}-${startDate[2]}T00:00:00"
        val et = "${endDate[0]}-${endDate[1]}-${endDate[2]}T00:00:00"
        adsUseCase.insertAds(
            insertAds = InsertAds(
                id = _state.value.id,
                title = _state.value.title,
                description = _state.value.description,
                advertiserName = _state.value.advertiserName,
                imageUrl = _state.value.imageUrl,
                targetUrl = _state.value.targetUrl,
                startDate = st,
                endDate = et,
                priority = _state.value.priority.toIntOrNull() ?: 0,
                status = "ACTIVE"
            )
        ) { response ->
            response.handelState(
                onLoading = {
                    updateState {
                        it.copy(isLoading = true)
                    }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            ads = _state.value.ads.toMutableList().apply {
                                add(0, data.data ?: Ads())
                            },
                            isLoading = false
                        )
                    }
                }, onError = { error, code ->
                    updateState {
                        it.copy(isLoading = false)
                    }
                }
            )
        }
    }

    private fun updateAds() = screenModelScope.launch {
        adsUseCase.updateAds(
            insertAds = InsertAds(
                id = _state.value.id,
                title = _state.value.title,
                description = _state.value.description,
                advertiserName = _state.value.advertiserName,
                imageUrl = _state.value.imageUrl,
                targetUrl = _state.value.targetUrl,
                startDate = _state.value.startDate,
                endDate = _state.value.endDate,
                priority = _state.value.priority.toIntOrNull() ?: 0
            )
        ) { response ->
            response.handelState(
                onLoading = {
                    updateState {
                        it.copy(isLoading = true)
                    }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            ads = _state.value.ads.toMutableList().map { ad ->
                                if (ad.id == _state.value.id) {
                                    data.data ?: Ads()
                                } else {
                                    ad
                                }
                            },
                            isLoading = false
                        )
                    }
                }, onError = { error, code ->
                    updateState {
                        it.copy(isLoading = false)
                    }
                }
            )
        }
    }
}
