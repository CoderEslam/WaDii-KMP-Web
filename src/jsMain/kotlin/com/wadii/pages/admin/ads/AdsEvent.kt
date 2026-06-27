package com.wadii.pages.admin.ads

import com.wadii.domain.model.ads.Ads

sealed class AdsEvent {
    data class ShowModal(val ad: Ads?) : AdsEvent()
    object CloseModal : AdsEvent()
    data class Delete(val adId: Long) : AdsEvent()
}