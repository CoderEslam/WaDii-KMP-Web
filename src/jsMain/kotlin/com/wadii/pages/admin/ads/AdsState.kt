package com.wadii.pages.admin.ads

import com.wadii.domain.model.ads.Ads

data class AdsState(
    val ads: List<Ads> = emptyList(),
    val isLoading: Boolean = false
)
