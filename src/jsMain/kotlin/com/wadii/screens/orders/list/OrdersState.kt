package com.wadii.screens.orders.list

import com.wadii.domain.model.order.OrderModel


data class OrdersState(
    val orders: List<OrderModel> = emptyList()
)
