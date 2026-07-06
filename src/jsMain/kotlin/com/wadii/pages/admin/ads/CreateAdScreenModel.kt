package com.wadii.pages.admin.ads

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.api.apiInsertAd
import com.wadii.data.api.apiUpdateAd
import com.wadii.domain.model.ads.Ads
import com.wadii.state.AppState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateAdScreenModel(
    private val existingAd: Ads?
) : BaseViewModel<CreateAdState, CreateAdEvent>() {

    override val initialState: CreateAdState get() = CreateAdState()

    override val state: StateFlow<CreateAdState> = _state

    init {
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
    }

    override fun onEvent(event: CreateAdEvent) {
        when (event) {
            is CreateAdEvent.SetTitle -> updateState { it.copy(title = event.value) }
            is CreateAdEvent.SetDescription -> updateState { it.copy(description = event.value) }
            is CreateAdEvent.SetAdvertiserName -> updateState { it.copy(advertiserName = event.value) }
            is CreateAdEvent.SetImageUrl -> updateState { it.copy(imageUrl = event.value) }
            is CreateAdEvent.SetTargetUrl -> updateState { it.copy(targetUrl = event.value) }
            is CreateAdEvent.SetStartDate -> updateState { it.copy(startDate = event.value) }
            is CreateAdEvent.SetEndDate -> updateState { it.copy(endDate = event.value) }
            is CreateAdEvent.SetPriority -> updateState { it.copy(priority = event.value) }
            CreateAdEvent.Submit -> submit()
        }
    }

    private fun submit() = screenModelScope.launch {
        val d = _state.value
        if (d.submitting) return@launch

        if (d.title.isBlank() || d.advertiserName.isBlank()) {
            updateState { it.copy(error = "Title and advertiser name are required") }
            return@launch
        }

        updateState { it.copy(submitting = true, error = null) }

        val body = mapOf(
            "id" to d.id,
            "title" to d.title,
            "description" to d.description,
            "advertiserName" to d.advertiserName,
            "imageUrl" to d.imageUrl,
            "targetUrl" to d.targetUrl,
            "startDate" to d.startDate,
            "endDate" to d.endDate,
            "priority" to (d.priority.toIntOrNull() ?: 0)
        )

        val result = if (d.isEdit) apiUpdateAd(body) else apiInsertAd(body)

        if (result != null) {
            AppState.toast(if (d.isEdit) "Ad updated!" else "Ad created!")
            updateState { it.copy(submitting = false, submitted = true) }
        } else {
            AppState.toast("Failed to save ad", true)
            updateState { it.copy(submitting = false, error = "Failed to save ad") }
        }
    }
}
