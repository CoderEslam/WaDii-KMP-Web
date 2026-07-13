package com.wadii.screens.orders.edit

sealed class EditOrderEvent {
    object Load : EditOrderEvent()
    data class SetCar(val value: String) : EditOrderEvent()
    data class SetComment(val value: String) : EditOrderEvent()
    data class ToggleService(val id: Long) : EditOrderEvent()
    data class SetSparePart(val index: Int, val value: String) : EditOrderEvent()
    object AddSparePart : EditOrderEvent()
    data class RemoveSparePart(val index: Int) : EditOrderEvent()
    object Submit : EditOrderEvent()
}
