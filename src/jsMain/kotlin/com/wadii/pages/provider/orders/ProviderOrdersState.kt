package com.wadii.pages.provider.orders

import com.wadii.domain.model.order.OrderModel


data class ProviderOrdersState(
    val orders: List<OrderModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
