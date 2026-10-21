package com.wadii.pages.admin.ads

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.ads.InsertAds
import com.wadii.domain.usecase.AdsUseCase
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.state.AppState
import com.wadii.utils.Validation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdsScreenModel(
    private val existingAd: Ads?,
    private val adsUseCase: AdsUseCase,
    private val marketplaceUseCase: MarketplaceUseCase
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
            is AdsEvent.SetStatus -> setStatus(event.adId, event.status)
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

    // PUT /advertisements/status/{id}/{status}: INACTIVE ads are never touched by the nightly scheduler.
    private fun setStatus(id: Long, status: String) = screenModelScope.launch {
        marketplaceUseCase.setAdStatus(id, status) { response ->
            response.handelState(
                onSuccess = { data ->
                    updateState { s -> s.copy(ads = s.ads.map { ad -> if (ad.id == id) data.data else ad }) }
                    AppState.toast(if (status == "ACTIVE") "Ad is live" else "Ad paused")
                },
                onError = { error, _ -> AppState.toast(error.ifBlank { "Couldn't change the ad status" }, true) }
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
                }, onError = { error, _ ->
                    updateState { it.copy(isLoading = false, error = error) }
                    AppState.toast(error.ifBlank { "Couldn't delete the advertisement" }, true)
                }
            )
        }
    }

    private fun submit() = screenModelScope.launch {
        val s = _state.value
        val errors = buildMap {
            if (s.title.isBlank()) put("title", "Title is required")
            if (s.advertiserName.isBlank()) put("advertiser", "Advertiser name is required")
            Validation.url(s.imageUrl)?.let { put("imageUrl", it) }
            Validation.url(s.targetUrl)?.let { put("targetUrl", it) }
            Validation.dateRange(s.startDate, s.endDate)?.let { put("dates", it) }
            val priority = s.priority.toIntOrNull()
            if (priority == null || priority !in 0..100) put("priority", "Priority is a whole number from 0 to 100 (higher shows first)")
        }
        updateState { it.copy(fieldErrors = errors, error = "") }
        if (errors.isNotEmpty()) return@launch

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
                    AppState.toast("Advertisement saved")
                    updateState {
                        it.copy(
                            ads = _state.value.ads.toMutableList().apply {
                                add(0, data.data ?: Ads())
                            },
                            isLoading = false
                        )
                    }
                }, onError = { error, _ ->
                    updateState { it.copy(isLoading = false, error = error) }
                    AppState.toast(error.ifBlank { "Couldn't save the advertisement" }, true)
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
                startDate = "${_state.value.startDate.take(10)}T00:00:00",
                endDate = "${_state.value.endDate.take(10)}T00:00:00",
                priority = _state.value.priority.toIntOrNull() ?: 0
            )
        ) { response ->
            response.handelState(
                onLoading = {
                    updateState {
                        it.copy(isLoading = true)
                    }
                }, onSuccess = { data ->
                    AppState.toast("Advertisement saved")
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
                }, onError = { error, _ ->
                    updateState { it.copy(isLoading = false, error = error) }
                    AppState.toast(error.ifBlank { "Couldn't save the advertisement" }, true)
                }
            )
        }
    }
}
