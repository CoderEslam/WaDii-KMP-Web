package com.wadii.pages.provider.respond

import com.wadii.domain.model.order.OrderModel

data class RespondToOrderState(
    val order: OrderModel = OrderModel(),
    val comment: String = "",
    val prices: List<Pair<Int, String>> = emptyList(),
    val submitting: Boolean = false,
    val submitted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = ""
)
