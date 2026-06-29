package com.wadii.screens.providerDetail

import com.wadii.domain.model.provider.ProviderModel

data class ProviderDetailState(
    val provider: ProviderModel? = null,
    val following: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
