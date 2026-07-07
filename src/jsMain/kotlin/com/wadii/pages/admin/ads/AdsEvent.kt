package com.wadii.pages.admin.ads

sealed class AdsEvent {
    data class Delete(val adId: Long) : AdsEvent()
    data class SetTitle(val value: String) : AdsEvent()
    data class SetDescription(val value: String) : AdsEvent()
    data class SetAdvertiserName(val value: String) : AdsEvent()
    data class SetImageUrl(val value: String) : AdsEvent()
    data class SetTargetUrl(val value: String) : AdsEvent()
    data class SetStartDate(val value: String) : AdsEvent()
    data class SetEndDate(val value: String) : AdsEvent()
    data class SetPriority(val value: String) : AdsEvent()
    object Submit : AdsEvent()
}
