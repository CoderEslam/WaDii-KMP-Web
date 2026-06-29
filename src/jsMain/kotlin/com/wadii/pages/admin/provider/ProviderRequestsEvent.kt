package com.wadii.pages.admin.provider

import com.wadii.domain.model.providerRequests.ProviderRequestModel

sealed class ProviderRequestsEvent {
    object Load : ProviderRequestsEvent()
    data class Accept(val request: ProviderRequestModel) : ProviderRequestsEvent()
}
