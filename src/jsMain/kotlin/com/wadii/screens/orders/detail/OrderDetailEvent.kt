package com.wadii.screens.orders.detail

sealed class OrderDetailEvent {
    data class Load(val orderId: Int) : OrderDetailEvent()
    data class AcceptResponse(val responseId: Long, val orderId: Int) : OrderDetailEvent()
    data class DeclineResponse(val responseId: Long, val orderId: Int) : OrderDetailEvent()
}
