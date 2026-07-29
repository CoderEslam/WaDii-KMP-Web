package com.wadii.pages.admin.dashboard

import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.providerRequests.ProviderRequestModel
import com.wadii.domain.model.service.Service

data class AdminState(
    val isLoading: Boolean = false,
    val requests: List<ProviderRequestModel> = emptyList(),
    val services: List<Service> = emptyList(),
    val ads: List<Ads> = emptyList(),
    val message: String = "",
//    val messageType : MessageType = MessageType.NONE
)
