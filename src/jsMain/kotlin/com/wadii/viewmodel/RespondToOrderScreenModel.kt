package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiGetOrder
import com.wadii.api.apiInsertResponse
import com.wadii.model.Order
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class RespondToOrderEvent {
    data class Load(val orderId: Long) : RespondToOrderEvent()
    data class SetComment(val value: String) : RespondToOrderEvent()
    data class SetPrice(val index: Int, val price: String) : RespondToOrderEvent()
    data class Submit(val orderId: Long) : RespondToOrderEvent()
}

data class RespondToOrderData(
    val order: Order,
    val comment: String = "",
    val prices: List<Pair<Long, String>> = emptyList(),
    val submitting: Boolean = false,
    val submitted: Boolean = false
)

class RespondToOrderScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<RespondToOrderData>>(UiState.Loading)
        private set

    fun onEvent(event: RespondToOrderEvent) = when (event) {
        is RespondToOrderEvent.Load -> load(event.orderId)
        is RespondToOrderEvent.SetComment -> mutate { copy(comment = event.value) }
        is RespondToOrderEvent.SetPrice -> mutate {
            copy(prices = prices.toMutableList().also { if (event.index < it.size) it[event.index] = it[event.index].first to event.price })
        }
        is RespondToOrderEvent.Submit -> submit(event.orderId)
    }

    private fun load(orderId: Long) {
        screenModelScope.launch {
            state = UiState.Loading
            val order = apiGetOrder(orderId)
            state = if (order != null)
                UiState.Success(RespondToOrderData(order, prices = order.spareParts?.map { it.id to "" } ?: emptyList()))
            else UiState.Error("Order not found.")
        }
    }

    private fun submit(orderId: Long) {
        val d = (state as? UiState.Success)?.data ?: return
        if (d.submitting) return
        mutate { copy(submitting = true) }
        screenModelScope.launch {
            val body = mapOf(
                "comment" to d.comment,
                "order" to mapOf("id" to orderId),
                "sparePartsPrice" to d.prices
                    .filter { (_, p) -> p.isNotBlank() }
                    .map { (id, p) -> mapOf("price" to p.toDoubleOrNull(), "spareParts" to mapOf("id" to id)) }
            )
            val resp = apiInsertResponse(body)
            if (resp != null) {
                AppState.toast("Response submitted!")
                mutate { copy(submitting = false, submitted = true) }
            } else {
                AppState.toast("Failed to submit", true)
                mutate { copy(submitting = false) }
            }
        }
    }

    private fun mutate(block: RespondToOrderData.() -> RespondToOrderData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
