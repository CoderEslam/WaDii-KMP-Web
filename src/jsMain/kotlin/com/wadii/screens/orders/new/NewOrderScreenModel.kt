package com.wadii.screens.orders.new

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.order.OrderRequest
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.state.AppState
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewOrderViewModel(
    private val orderUseCase: OrderUseCase,
    private val servicesUseCase: ServicesUseCase
) : BaseViewModel<NewOrderState, NewOrderEvent>() {

    override val initialState: NewOrderState
        get() = NewOrderState()

    override val state: StateFlow<NewOrderState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: NewOrderEvent) {
        when (event) {
            NewOrderEvent.Load -> load()
            is NewOrderEvent.SetCar -> updateState { it.copy(carModelYear = event.value) }
            is NewOrderEvent.SetComment -> updateState { it.copy(comment = event.value) }
            is NewOrderEvent.ToggleService -> updateState {
                it.copy(selectedServices = if (event.id in it.selectedServices) it.selectedServices - event.id else it.selectedServices + event.id)
            }
            is NewOrderEvent.SetSparePart -> updateState {
                it.copy(spareParts = it.spareParts.toMutableList().also { list -> list[event.index] = event.value })
            }
            NewOrderEvent.AddSparePart -> updateState { it.copy(spareParts = it.spareParts + "") }
            is NewOrderEvent.RemoveSparePart -> updateState {
                it.copy(spareParts = it.spareParts.toMutableList().also { list -> list.removeAt(event.index) })
            }
            NewOrderEvent.Submit -> submit()
        }
    }

    private fun load() = screenModelScope.launch {
        servicesUseCase.getServiceList { response ->
            response.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(services = data.data ?: emptyList(), isLoading = false) } },
                onError = { error, _ -> updateState { it.copy(error = error, isLoading = false) } }
            )
        }
    }

    private fun submit() = screenModelScope.launch {
        val d = _state.value
        if (d.submitting) return@launch
        updateState { it.copy(submitting = true) }
        orderUseCase.insertOrder(
            OrderRequest(
                carModelYear = d.carModelYear,
                comment = d.comment,
                date = js("new Date().toISOString()").toString(),
                servicesIds = d.selectedServices.toList(),
                spareParts = d.spareParts.filter { it.isNotBlank() }.map { OrderRequest.SparePart(it) }
            )
        ) { response ->
            response.handelState(
                onLoading = {},
                onSuccess = { _ ->
                    AppState.toast("Order created!")
                    updateState { it.copy(submitting = false, submitted = true) }
                },
                onError = { _, _ ->
                    AppState.toast("Failed to create order", true)
                    updateState { it.copy(submitting = false) }
                }
            )
        }
    }
}
