package com.wadii.screens.providerDetail

import com.wadii.domain.model.provider.ProviderModel


data class ProviderDetailState(
    val provider: ProviderModel,
    val following: Boolean = false
)
