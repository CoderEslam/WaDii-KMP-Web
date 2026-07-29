package com.wadii.screens.orders.list

import com.wadii.domain.model.order.CancelReason
import com.wadii.domain.model.order.OrderModel

data class OrdersState(
    val orders: List<OrderModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val cancelingOrder: OrderModel? = null,
    val cancelReasons: List<CancelReason> = emptyList(),
    val loadingCancelReasons: Boolean = false,
    val selectedReasonId: Int? = null,
    val isCancelling: Boolean = false
)
