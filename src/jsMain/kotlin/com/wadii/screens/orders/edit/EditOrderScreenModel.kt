package com.wadii.screens.orders.edit

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

class EditOrderViewModel(
    private val orderId: Int,
    private val orderUseCase: OrderUseCase,
    private val servicesUseCase: ServicesUseCase
) : BaseViewModel<EditOrderState, EditOrderEvent>() {

    override val initialState: EditOrderState
        get() = EditOrderState()

    override val state: StateFlow<EditOrderState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: EditOrderEvent) {
        when (event) {
            EditOrderEvent.Load -> load()
            is EditOrderEvent.SetCar -> updateState { it.copy(carModelYear = event.value) }
            is EditOrderEvent.SetComment -> updateState { it.copy(comment = event.value) }
            is EditOrderEvent.ToggleService -> updateState {
                it.copy(selectedServices = if (event.id in it.selectedServices) it.selectedServices - event.id else it.selectedServices + event.id)
            }

            is EditOrderEvent.SetSparePart -> updateState {
                it.copy(
                    spareParts = it.spareParts.toMutableList()
                        .also { list -> list[event.index] = event.value })
            }

            EditOrderEvent.AddSparePart -> updateState { it.copy(spareParts = it.spareParts + "") }
            is EditOrderEvent.RemoveSparePart -> updateState {
                it.copy(
                    spareParts = it.spareParts.toMutableList()
                        .also { list -> list.removeAt(event.index) })
            }

            EditOrderEvent.Submit -> submit()
        }
    }

    private fun load() = screenModelScope.launch {
        updateState { it.copy(isLoading = true) }

        servicesUseCase.getServiceList { response ->
            response.handelState(
                onSuccess = { data -> updateState { it.copy(services = data.data ?: emptyList()) } },
                onError = { error, _ -> updateState { it.copy(error = error) } }
            )
        }

        orderUseCase.getOrderById(orderId) { response ->
            response.handelState(
                onSuccess = { data ->
                    val order = data.data
                    updateState {
                        it.copy(
                            carModelYear = order.carModelYear,
                            comment = order.comment,
                            selectedServices = order.services.map { s -> s.id.toLong() }.toSet(),
                            spareParts = order.spareParts.map { sp -> sp.sparePartName }
                                .ifEmpty { listOf("") },
                            isLoading = false
                        )
                    }
                },
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
                id = orderId.toString(),
                userId = AppState.user?.id ?: 0L,
                carModelYear = d.carModelYear,
                comment = d.comment,
                date = js("new Date().toISOString()").toString(),
                servicesIds = d.selectedServices.toList(),
                spareParts = d.spareParts.filter { it.isNotBlank() }
                    .map { OrderRequest.SparePart(it) }
            )
        ) { response ->
            response.handelState(
                onSuccess = { _ ->
                    AppState.toast("Order updated!")
                    updateState { it.copy(submitting = false, submitted = true) }
                },
                onError = { _, _ ->
                    AppState.toast("Failed to update order", true)
                    updateState { it.copy(submitting = false) }
                }
            )
        }
    }
}
