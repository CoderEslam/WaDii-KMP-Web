package com.wadii.pages.admin.provider

import com.wadii.domain.model.provider.ProviderRequest

sealed class ProviderRequestsEvent {
    object Load : ProviderRequestsEvent()
    data class Accept(val request: ProviderRequest) : ProviderRequestsEvent()
}
