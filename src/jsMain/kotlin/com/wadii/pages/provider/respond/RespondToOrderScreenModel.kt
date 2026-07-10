package com.wadii.pages.provider.respond

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.response.ResponseRequest
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.domain.usecase.ResponseUseCase
import com.wadii.state.AppState
import com.wadii.utils.getCurrentLocation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RespondToOrderViewModel(
    private val orderId: Int,
    private val orderUseCase: OrderUseCase,
    private val responseUseCase: ResponseUseCase
) : BaseViewModel<RespondToOrderState, RespondToOrderEvent>() {

    override val initialState: RespondToOrderState get() = RespondToOrderState()

    override val state: StateFlow<RespondToOrderState> = _state
        .onStart { load() }
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    override fun onEvent(event: RespondToOrderEvent) {
        when (event) {
            is RespondToOrderEvent.SetComment -> updateState { it.copy(comment = event.value) }
            is RespondToOrderEvent.SetPrice -> updateState {
                it.copy(prices = it.prices.toMutableList().also { list ->
                    if (event.index < list.size) list[event.index] =
                        list[event.index].first to event.price
                })
            }

            is RespondToOrderEvent.Submit -> submit()
            else -> Unit
        }
    }

    private fun load() = screenModelScope.launch {
        orderUseCase.getOrderById(orderId) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    val order = data.data
                    updateState {
                        it.copy(
                            order = order,
                            prices = order?.spareParts?.map { sp -> sp.id to "" } ?: emptyList(),
                            isLoading = false
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun submit() = screenModelScope.launch {
        val s = _state.value
        if (s.submitting) return@launch

        val blankPrice = s.prices.any { (_, price) -> price.isBlank() }
        if (blankPrice) {
            _state.update { it.copy(error = "Please enter a price for every spare part") }
            return@launch
        }

        _state.update { it.copy(submitting = true) }

        val location = getCurrentLocation()
        val providerId = AppState.user?.provider?.id ?: 0L
        val request = ResponseRequest(
            providerId = providerId,
            orderId = orderId,
            comment = s.comment.trim(),
            sparePartsPrice = s.prices.map { (id, price) ->
                ResponseRequest.SparePartPrice(id = id, price = price.toDoubleOrNull() ?: 0.0)
            },
            latitude = location?.first ?: 0.0,
            longitude = location?.second ?: 0.0
        )

        responseUseCase.createResponse(request) { response ->
            response.handelState(
                onLoading = { _state.update { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            submitting = false,
                            submitted = true,
                            error = data.message.ifBlank { "Response sent successfully" },
                        )
                    }
                    AppState.toast("Response submitted!")
                },
                onError = { error, _ ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            submitting = false,
                            error = error,
                        )
                    }
                    AppState.toast("Failed to submit", true)
                }
            )
        }
    }
}
