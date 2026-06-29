package com.wadii.pages.provider.respond

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.api.apiInsertResponse
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RespondToOrderViewModel(
    private val orderId: Int,
    private val orderUseCase: OrderUseCase
) : BaseViewModel<RespondToOrderState, RespondToOrderEvent>() {

    override val initialState: RespondToOrderState get() = RespondToOrderState()

    override val state: StateFlow<RespondToOrderState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: RespondToOrderEvent) {
        when (event) {
            is RespondToOrderEvent.SetComment -> updateState { it.copy(comment = event.value) }
            is RespondToOrderEvent.SetPrice -> updateState {
                it.copy(prices = it.prices.toMutableList().also { list ->
                    if (event.index < list.size) list[event.index] = list[event.index].first to event.price
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

    private fun submit() {
        val current = _state.value
        if (current.submitting || current.order == null) return
        updateState { it.copy(submitting = true) }
        screenModelScope.launch {
            val body = mapOf(
                "comment" to current.comment,
                "order" to mapOf("id" to orderId),
                "sparePartsPrice" to current.prices
                    .filter { (_, p) -> p.isNotBlank() }
                    .map { (id, p) -> mapOf("price" to p.toDoubleOrNull(), "spareParts" to mapOf("id" to id)) }
            )
            val resp = apiInsertResponse(body)
            if (resp != null) {
                AppState.toast("Response submitted!")
                updateState { it.copy(submitting = false, submitted = true) }
            } else {
                AppState.toast("Failed to submit", true)
                updateState { it.copy(submitting = false) }
            }
        }
    }
}
