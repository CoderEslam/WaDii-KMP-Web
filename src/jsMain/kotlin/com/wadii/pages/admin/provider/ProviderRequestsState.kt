package com.wadii.pages.admin.provider

import com.wadii.domain.model.provider.ProviderRequest


data class ProviderRequestsState(
    val requests: List<ProviderRequest> = emptyList(),
    val acceptingId: Long? = null
)
