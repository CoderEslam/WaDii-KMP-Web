package com.wadii.screens.orders.edit

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.order.OrderRequest
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.utils.RequestValidators
import com.wadii.utils.firstMessage
import com.wadii.screens.orders.canEdit
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
    private val servicesUseCase: ServicesUseCase,
    private val marketplaceUseCase: MarketplaceUseCase
) : BaseViewModel<EditOrderState, EditOrderEvent>() {

    override val initialState: EditOrderState
        get() = EditOrderState()

    override val state: StateFlow<EditOrderState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: EditOrderEvent) {
        when (event) {
            EditOrderEvent.Load -> load()
            is EditOrderEvent.SetCar -> updateState { it.copy(carModelYear = event.value, fieldErrors = it.fieldErrors - "car") }
            is EditOrderEvent.SetDate -> updateState { it.copy(date = event.value, fieldErrors = it.fieldErrors - "date") }
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
                    if (!order.canEdit) {
                        updateState { it.copy(error = "Only orders without proposals can be edited. Cancel this order and create a new one.", isLoading = false) }
                    } else updateState {
                        it.copy(
                            carModelYear = order.carModelYear,
                            date = order.date.take(10),
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
        val errors = RequestValidators.order(
            carModelYear = d.carModelYear,
            date = d.date,
            serviceIds = d.selectedServices,
            servicesAvailable = d.services.isNotEmpty(),
            orderId = orderId
        )
        updateState { it.copy(fieldErrors = errors) }
        if (errors.isNotEmpty()) {
            AppState.toast(errors.firstMessage(), true)
            return@launch
        }
        updateState { it.copy(submitting = true) }
        // POST /orders/update (this used to call insert, which created a duplicate order instead of editing).
        marketplaceUseCase.updateOrder(
            OrderRequest(
                id = orderId.toString(),
                carModelYear = d.carModelYear,
                comment = d.comment,
                date = d.date,
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
                onError = { error, _ ->
                    AppState.toast(if (error.isNotBlank()) "Couldn't save your changes: $error" else "Couldn't save your changes. Please try again.", true)
                    updateState { it.copy(submitting = false) }
                }
            )
        }
    }
}
