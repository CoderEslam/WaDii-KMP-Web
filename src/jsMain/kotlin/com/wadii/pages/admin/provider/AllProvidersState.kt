package com.wadii.pages.admin.provider

import com.wadii.domain.model.provider.ProviderModel

data class AllProvidersState(
    val providers: List<ProviderModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
