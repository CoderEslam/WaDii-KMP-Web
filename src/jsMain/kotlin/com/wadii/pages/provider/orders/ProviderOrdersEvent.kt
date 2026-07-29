package com.wadii.pages.provider.orders

sealed class ProviderOrdersEvent {
    object Load : ProviderOrdersEvent()
}
