package com.wadii.pages.provider.dashboard

import com.wadii.domain.model.order.OrderModel
import com.wadii.domain.model.provider.ProviderModel


data class ProviderDashboardState(
    val provider: ProviderModel? = null,
    val orders: List<OrderModel> = emptyList()
)
