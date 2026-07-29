package com.wadii.pages.admin.provider

import com.wadii.domain.model.providerRequests.ProviderRequestModel

sealed class ProviderRequestsEvent {
    data class Accept(val request: ProviderRequestModel) : ProviderRequestsEvent()

    data class Reject(val request: ProviderRequestModel) : ProviderRequestsEvent()
}
