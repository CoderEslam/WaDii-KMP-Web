package com.wadii.screens.orders.list

import com.wadii.domain.model.order.OrderModel

sealed class OrdersEvent {
    object Load : OrdersEvent()
    data class OpenCancelDialog(val order: OrderModel) : OrdersEvent()
    object DismissCancelDialog : OrdersEvent()
    data class SetCancelReason(val value: String) : OrdersEvent()
    object ConfirmCancel : OrdersEvent()
}
