package com.wadii.screens.orders.list

import com.wadii.domain.model.order.OrderModel

data class OrdersState(
    val orders: List<OrderModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val cancelingOrder: OrderModel? = null,
    val cancelReason: String = "",
    val isCancelling: Boolean = false
)
