package com.wadii.pages.admin.provider

import com.wadii.domain.model.providerRequests.ProviderRequestModel

sealed class ProviderRequestsEvent {
    data class Accept(val request: ProviderRequestModel) : ProviderRequestsEvent()

    /** Opens the reject dialog. */
    data class Reject(val request: ProviderRequestModel) : ProviderRequestsEvent()
    data class SetReason(val value: String) : ProviderRequestsEvent()
    object DismissReject : ProviderRequestsEvent()
    object ConfirmReject : ProviderRequestsEvent()
}
