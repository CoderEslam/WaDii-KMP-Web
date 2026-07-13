package com.wadii.pages.admin.reason

import com.wadii.domain.model.order.CancelReason


data class ReasonsState(
    val reasons: List<CancelReason> = emptyList(),
    val editingId: Int = 0,
    val editReason: String = "",
    val newReason: String = "",
    val saving: Boolean = false,
    val adding: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
