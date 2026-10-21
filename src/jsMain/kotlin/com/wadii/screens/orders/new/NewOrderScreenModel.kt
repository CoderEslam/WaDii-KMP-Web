package com.wadii.screens.orders.new

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.order.OrderRequest
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.utils.RequestValidators
import com.wadii.utils.firstMessage
import com.wadii.state.AppState
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewOrderViewModel(
    /** 0 = send to every matching seller; otherwise a direct order to this provider only. */
    private val providerId: Long,
    /** For a direct order: the services this provider offers (the server rejects anything else). */
    private val offeredServiceIds: List<Long>,
    private val orderUseCase: OrderUseCase,
    private val servicesUseCase: ServicesUseCase,
    private val marketplaceUseCase: MarketplaceUseCase
) : BaseViewModel<NewOrderState, NewOrderEvent>() {

    override val initialState: NewOrderState
        get() = NewOrderState()

    override val state: StateFlow<NewOrderState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: NewOrderEvent) {
        when (event) {
            NewOrderEvent.Load -> load()
            is NewOrderEvent.SetCarMake -> updateState {
                it.copy(carMake = event.name, carModelYear = "${event.name} ${it.carDetails}".trim(), fieldErrors = it.fieldErrors - "car")
            }
            is NewOrderEvent.SetCar -> updateState {
                it.copy(carDetails = event.value, carModelYear = "${it.carMake} ${event.value}".trim(), fieldErrors = it.fieldErrors - "car")
            }
            is NewOrderEvent.SetDate -> updateState { it.copy(date = event.value, fieldErrors = it.fieldErrors - "date") }
            is NewOrderEvent.SetComment -> updateState { it.copy(comment = event.value) }
            is NewOrderEvent.ToggleService -> updateState {
                it.copy(
                    selectedServices = if (event.id in it.selectedServices) it.selectedServices - event.id else it.selectedServices + event.id,
                    fieldErrors = it.fieldErrors - "services"
                )
            }

            is NewOrderEvent.SetSparePart -> updateState {
                it.copy(
                    spareParts = it.spareParts.toMutableList()
                        .also { list -> list[event.index] = event.value },
                    fieldErrors = it.fieldErrors - "parts"
                )
            }

            NewOrderEvent.AddSparePart -> updateState { it.copy(spareParts = it.spareParts + "") }
            // Fills the first empty box, otherwise appends a new one.
            is NewOrderEvent.UseSuggestion -> updateState { s ->
                val emptyAt = s.spareParts.indexOfFirst { it.isBlank() }
                s.copy(
                    spareParts = if (emptyAt >= 0) s.spareParts.toMutableList().also { it[emptyAt] = event.name } else s.spareParts + event.name,
                    fieldErrors = s.fieldErrors - "parts"
                )
            }
            is NewOrderEvent.RemoveSparePart -> updateState {
                it.copy(
                    spareParts = it.spareParts.toMutableList()
                        .also { list -> list.removeAt(event.index) })
            }

            NewOrderEvent.Submit -> submit()
        }
    }

    private fun load() = screenModelScope.launch {
        // Suggestions are a nicety: if they fail to load the form still works.
        marketplaceUseCase.getCommonSpareParts { r ->
            r.handelState(onSuccess = { data -> updateState { it.copy(suggestions = data.data) } })
        }
        orderUseCase.getCarTypeList { r ->
            r.handelState(onSuccess = { data -> updateState { it.copy(carTypes = data.data) } })
        }
        servicesUseCase.getServiceList { response ->
            response.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            services = (data.data ?: emptyList()).filter { s -> offeredServiceIds.isEmpty() || s.id in offeredServiceIds },
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
        val errors = (RequestValidators.order(
            carModelYear = d.carModelYear,
            date = d.date,
            serviceIds = d.selectedServices,
            servicesAvailable = d.services.isNotEmpty()
        ) - "car") + when {
            d.carTypes.isNotEmpty() && d.carMake.isBlank() -> mapOf("car" to "Choose your car's make")
            d.carDetails.isBlank() -> mapOf("car" to "Add the model and year, e.g. Camry 2022")
            else -> emptyMap()
        }
        updateState { it.copy(fieldErrors = errors) }
        if (errors.isNotEmpty()) {
            AppState.toast(errors.firstMessage(), true)
            return@launch
        }
        updateState { it.copy(submitting = true) }
        orderUseCase.insertOrder(
            OrderRequest(
                carModelYear = d.carModelYear,
                comment = d.comment,
                date = d.date,
                providerId = providerId.takeIf { it > 0 },
                servicesIds = d.selectedServices.toList(),
                spareParts = d.spareParts.filter { it.isNotBlank() }
                    .map { OrderRequest.SparePart(it) }
            )
        ) { response ->
            response.handelState(
                onLoading = {
//                    updateState { it.copy(submitting = true) }
                },
                onSuccess = { _ ->
                    AppState.toast(if (providerId > 0) "Request sent to the seller" else "Order created! Matching sellers have been notified.")
                    updateState { it.copy(submitting = false, submitted = true) }
                },
                onError = { error, _ ->
                    AppState.toast(
                        if (!error.isNullOrBlank()) "Couldn't create your order: $error"
                        else "Couldn't create your order. Check your connection and try again.",
                        true
                    )
                    updateState { it.copy(submitting = false) }
                }
            )
        }
    }
}
