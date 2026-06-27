package com.wadii.screens.providerDetail

sealed class ProviderDetailEvent {
    data class Load(val providerId: Int) : ProviderDetailEvent()
    data class ToggleFollow(val providerId: Int) : ProviderDetailEvent()
}
