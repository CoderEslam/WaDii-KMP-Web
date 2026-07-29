package com.wadii.screens.orders.new

sealed class NewOrderEvent {
    object Load : NewOrderEvent()
    data class SetCar(val value: String) : NewOrderEvent()
    data class SetComment(val value: String) : NewOrderEvent()
    data class ToggleService(val id: Long) : NewOrderEvent()
    data class SetSparePart(val index: Int, val value: String) : NewOrderEvent()
    object AddSparePart : NewOrderEvent()
    data class RemoveSparePart(val index: Int) : NewOrderEvent()
    object Submit : NewOrderEvent()
}
