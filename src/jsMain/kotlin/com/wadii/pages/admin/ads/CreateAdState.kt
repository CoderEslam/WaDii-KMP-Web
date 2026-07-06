package com.wadii.pages.admin.ads

data class CreateAdState(
    val id: Int? = null,
    val title: String = "",
    val description: String = "",
    val advertiserName: String = "",
    val imageUrl: String = "",
    val targetUrl: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val priority: String = "0",
    val submitting: Boolean = false,
    val submitted: Boolean = false,
    val error: String? = null
) {
    val isEdit: Boolean get() = id != null
}
