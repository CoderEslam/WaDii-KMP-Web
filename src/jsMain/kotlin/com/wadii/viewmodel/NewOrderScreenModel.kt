package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiGetAllServices
import com.wadii.api.apiInsertOrder
import com.wadii.model.Service
import com.wadii.state.AppState
import kotlinx.coroutines.launch

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

data class NewOrderData(
    val services: List<Service> = emptyList(),
    val carModelYear: String = "",
    val comment: String = "",
    val selectedServices: Set<Long> = emptySet(),
    val spareParts: List<String> = listOf(""),
    val submitting: Boolean = false,
    val submitted: Boolean = false
)

class NewOrderScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<NewOrderData>>(UiState.Loading)
        private set

    init { onEvent(NewOrderEvent.Load) }

    fun onEvent(event: NewOrderEvent) = when (event) {
        NewOrderEvent.Load -> load()
        is NewOrderEvent.SetCar -> mutate { copy(carModelYear = event.value) }
        is NewOrderEvent.SetComment -> mutate { copy(comment = event.value) }
        is NewOrderEvent.ToggleService -> mutate {
            copy(selectedServices = if (event.id in selectedServices) selectedServices - event.id else selectedServices + event.id)
        }
        is NewOrderEvent.SetSparePart -> mutate {
            copy(spareParts = spareParts.toMutableList().also { it[event.index] = event.value })
        }
        NewOrderEvent.AddSparePart -> mutate { copy(spareParts = spareParts + "") }
        is NewOrderEvent.RemoveSparePart -> mutate {
            copy(spareParts = spareParts.toMutableList().also { it.removeAt(event.index) })
        }
        NewOrderEvent.Submit -> submit()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            state = UiState.Success(NewOrderData(services = apiGetAllServices()))
        }
    }

    private fun submit() {
        val d = (state as? UiState.Success)?.data ?: return
        if (d.submitting) return
        mutate { copy(submitting = true) }
        screenModelScope.launch {
            val body = mapOf(
                "carModelYear" to d.carModelYear,
                "comment" to d.comment,
                "date" to js("new Date().toISOString()"),
                "services" to d.selectedServices.map { mapOf("id" to it) },
                "spareParts" to d.spareParts.filter { it.isNotBlank() }.map { mapOf("sparePartName" to it) }
            )
            val order = apiInsertOrder(body)
            if (order != null) {
                AppState.toast("Order created!")
                mutate { copy(submitting = false, submitted = true) }
            } else {
                AppState.toast("Failed to create order", true)
                mutate { copy(submitting = false) }
            }
        }
    }

    private fun mutate(block: NewOrderData.() -> NewOrderData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
