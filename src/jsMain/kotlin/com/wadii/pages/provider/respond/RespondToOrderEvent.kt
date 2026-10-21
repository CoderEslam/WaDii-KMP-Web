package com.wadii.pages.provider.respond

sealed class RespondToOrderEvent {
    data class Load(val orderId: Int) : RespondToOrderEvent()
    data class SetComment(val value: String) : RespondToOrderEvent()
    data class SetPrice(val index: Int, val price: String) : RespondToOrderEvent()
    data class Submit(val orderId: Int) : RespondToOrderEvent()
    object StartEdit : RespondToOrderEvent()
    object CancelEdit : RespondToOrderEvent()
    object Withdraw : RespondToOrderEvent()
    object StartJob : RespondToOrderEvent()
}
