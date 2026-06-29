package com.wadii.screens.orders.detail

import com.wadii.domain.model.order.OrderModel

data class OrderDetailState(
    val order: OrderModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
