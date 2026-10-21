package com.wadii.pages.provider.respond

import com.wadii.domain.model.order.OrderModel

data class RespondToOrderState(
    val order: OrderModel = OrderModel(),
    val comment: String = "",
    val prices: List<Pair<Int, String>> = emptyList(),
    val submitting: Boolean = false,
    val submitted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = "",
    val fieldErrors: Map<String, String> = emptyMap(),
    /** True while the pending answer is being edited in the form. */
    val editing: Boolean = false,
    /** Withdrawing or starting the job. */
    val busy: Boolean = false
)
