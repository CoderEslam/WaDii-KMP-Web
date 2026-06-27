package com.wadii.screens.orders.list

sealed class OrdersEvent {
    object Load : OrdersEvent()
}
