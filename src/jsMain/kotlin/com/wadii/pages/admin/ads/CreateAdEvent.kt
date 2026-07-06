package com.wadii.pages.admin.ads

sealed class CreateAdEvent {
    data class SetTitle(val value: String) : CreateAdEvent()
    data class SetDescription(val value: String) : CreateAdEvent()
    data class SetAdvertiserName(val value: String) : CreateAdEvent()
    data class SetImageUrl(val value: String) : CreateAdEvent()
    data class SetTargetUrl(val value: String) : CreateAdEvent()
    data class SetStartDate(val value: String) : CreateAdEvent()
    data class SetEndDate(val value: String) : CreateAdEvent()
    data class SetPriority(val value: String) : CreateAdEvent()
    object Submit : CreateAdEvent()
}
