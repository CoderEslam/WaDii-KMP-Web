package com.wadii.pages.admin.ads

sealed class AdsEvent {
    object Load : AdsEvent()
    data class Delete(val adId: Long) : AdsEvent()
}
