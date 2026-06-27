package com.wadii.pages.provider.respond

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiGetOrder
import com.wadii.data.api.apiInsertResponse
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class RespondToOrderScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<RespondToOrderState>>(UiState.Loading)
        private set

    fun onEvent(event: RespondToOrderEvent) = when (event) {
        is RespondToOrderEvent.Load -> load(event.orderId)
        is RespondToOrderEvent.SetComment -> mutate { copy(comment = event.value) }
        is RespondToOrderEvent.SetPrice -> mutate {
            copy(prices = prices.toMutableList().also { if (event.index < it.size) it[event.index] = it[event.index].first to event.price })
        }
        is RespondToOrderEvent.Submit -> submit(event.orderId)
    }

    private fun load(orderId: Int) {
        screenModelScope.launch {
            state = UiState.Loading
            val order = apiGetOrder(orderId)
            state = if (order != null)
                UiState.Success(RespondToOrderState(order, prices = order.spareParts?.map { it.id to "" } ?: emptyList()))
            else UiState.Error("Order not found.")
        }
    }

    private fun submit(orderId: Int) {
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

    private fun mutate(block: RespondToOrderState.() -> RespondToOrderState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
