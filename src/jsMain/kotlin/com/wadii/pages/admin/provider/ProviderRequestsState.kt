package com.wadii.pages.admin.provider

import com.wadii.domain.model.providerRequests.ProviderRequestModel

data class ProviderRequestsState(
    val requests: List<ProviderRequestModel> = emptyList(),
    val acceptingId: Long = 0L,
    val rejectingId: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null
)
