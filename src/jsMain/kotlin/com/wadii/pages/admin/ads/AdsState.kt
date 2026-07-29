package com.wadii.pages.admin.ads

import com.wadii.domain.model.ads.Ads

data class AdsState(
    val ads: List<Ads> = emptyList(),
    val isLoading: Boolean = false,
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val advertiserName: String = "",
    val imageUrl: String = "",
    val targetUrl: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val priority: String = "0",
    val error: String = ""
) {
    val isEdit: Boolean get() = id != 0L
}
