package com.wadii.screens.orders.detail

sealed class OrderDetailEvent {
    data class Load(val orderId: Int) : OrderDetailEvent()
    data class AcceptResponse(val responseId: Long, val orderId: Int) : OrderDetailEvent()
    data class DeclineResponse(val responseId: Long, val orderId: Int) : OrderDetailEvent()
    object Complete : OrderDetailEvent()
    data class SetRating(val stars: Int) : OrderDetailEvent()
    data class SetRatingComment(val value: String) : OrderDetailEvent()
    object SubmitRating : OrderDetailEvent()
}
