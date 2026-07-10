package com.wadii.screens.providerDetail

sealed class ProviderDetailEvent {
    data class Load(val providerId: Long) : ProviderDetailEvent()
    data class ToggleFollow(val providerId: Long) : ProviderDetailEvent()
}
