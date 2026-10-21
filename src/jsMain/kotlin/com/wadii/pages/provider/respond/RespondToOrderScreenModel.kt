package com.wadii.pages.provider.respond

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.order.OrderModel
import com.wadii.domain.model.response.ResponseRequest
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.domain.usecase.ResponseUseCase
import com.wadii.screens.orders.canStart
import com.wadii.state.AppState
import com.wadii.utils.RequestValidators
import com.wadii.utils.firstMessage
import com.wadii.utils.getCurrentLocation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * One order as the invited provider sees it: answer it, edit or withdraw the pending answer, and once the
 * customer accepted, start the job (API_REFERENCE.md "Order lifecycle").
 */
class RespondToOrderViewModel(
    private val orderId: Int,
    private val orderUseCase: OrderUseCase,
    private val responseUseCase: ResponseUseCase,
    private val marketplaceUseCase: MarketplaceUseCase
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
            is RespondToOrderEvent.SetComment -> updateState { it.copy(comment = event.value, fieldErrors = it.fieldErrors - "comment") }
            is RespondToOrderEvent.SetPrice -> updateState {
                it.copy(
                    prices = it.prices.toMutableList().also { list ->
                        if (event.index < list.size) list[event.index] = list[event.index].first to event.price
                    },
                    fieldErrors = it.fieldErrors - "price${event.index}"
                )
            }

            is RespondToOrderEvent.Submit -> submit()
            RespondToOrderEvent.StartEdit -> startEdit()
            RespondToOrderEvent.CancelEdit -> updateState { it.copy(editing = false, fieldErrors = emptyMap()) }
            RespondToOrderEvent.Withdraw -> withdraw()
            RespondToOrderEvent.StartJob -> startJob()
            else -> Unit
        }
    }

    /** The provider's own answer: the server returns only that one in `responses`. */
    private fun OrderModel.mine(): OrderModel.OrderResponse? {
        val myProviderId = AppState.user?.provider?.id
        return responses.firstOrNull { it.provider.id == myProviderId } ?: responses.firstOrNull()
    }

    private fun load() = screenModelScope.launch {
        orderUseCase.getOrderById(orderId) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = it.order.id == 0) } },
                onSuccess = { data ->
                    val order = data.data
                    updateState {
                        it.copy(
                            order = order,
                            prices = order.spareParts.map { sp -> sp.id to "" },
                            isLoading = false,
                            error = ""
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    /** Opens the form pre-filled with the pending answer. */
    private fun startEdit() {
        val s = _state.value
        val mine = s.order.mine() ?: return
        val priceBySparePart = mine.sparePartsPrices.associate { it.sparePart.id to it.price }
        updateState {
            it.copy(
                editing = true,
                comment = mine.comment,
                prices = s.order.spareParts.map { sp -> sp.id to (priceBySparePart[sp.id]?.toString() ?: "") },
                fieldErrors = emptyMap()
            )
        }
    }

    private fun submit() = screenModelScope.launch {
        val s = _state.value
        if (s.submitting) return@launch
        val editingId = if (s.editing) s.order.mine()?.id else null

        val errors = RequestValidators.response(
            comment = s.comment,
            orderId = orderId,
            prices = s.prices.map { it.second },
            responseId = editingId
        ).toMutableMap()
        // Unlike the generic rule, a proposal needs a price for every requested part.
        s.prices.forEachIndexed { i, (_, price) ->
            if (price.isBlank()) errors["price$i"] = "Enter a price for this part"
        }
        _state.update { it.copy(fieldErrors = errors, error = "") }
        if (errors.isNotEmpty()) {
            AppState.toast(errors.firstMessage(), true)
            return@launch
        }

        _state.update { it.copy(submitting = true) }

        val location = getCurrentLocation()
        val request = ResponseRequest(
            id = editingId ?: 0,
            orderId = orderId,
            comment = s.comment.trim(),
            sparePartsPrice = s.prices.map { (id, price) ->
                ResponseRequest.SparePartPrice(id = id, price = price.toDoubleOrNull() ?: 0.0)
            },
            latitude = location?.first ?: 0.0,
            longitude = location?.second ?: 0.0
        )

        val handle: (com.wadii.utils.RequestState<com.wadii.domain.model.BaseResponse<com.wadii.domain.model.response.ResponseCallback>>) -> Unit = { response ->
            response.handelState(
                onSuccess = {
                    _state.update { it.copy(submitting = false, submitted = true, error = "") }
                    AppState.toast(if (editingId != null) "Response updated" else "Response sent! The customer has been notified.")
                },
                onError = { error, _ ->
                    _state.update { it.copy(submitting = false, error = error) }
                    AppState.toast(error.ifBlank { "Couldn't send your response. Please try again." }, true)
                    load() // a 409 usually means the order is no longer open
                }
            )
        }
        if (editingId != null) marketplaceUseCase.updateResponse(request, handle) else responseUseCase.createResponse(request, handle)
    }

    /** DELETE /responses/delete/{id}: withdraws the pending answer. The server then blocks answering this order again. */
    private fun withdraw() = screenModelScope.launch {
        val s = _state.value
        val mine = s.order.mine() ?: return@launch
        if (s.busy) return@launch
        updateState { it.copy(busy = true) }
        marketplaceUseCase.deleteResponse(mine.id) { r ->
            r.handelState(
                onSuccess = {
                    AppState.toast("Response withdrawn")
                    updateState { it.copy(busy = false, editing = false) }
                    load()
                },
                onError = { error, _ ->
                    AppState.toast(error.ifBlank { "Couldn't withdraw your response" }, true)
                    updateState { it.copy(busy = false) }
                    load()
                }
            )
        }
    }

    /** POST /orders/start/{id}: only the accepted provider, only from ACCEPTED. */
    private fun startJob() = screenModelScope.launch {
        val s = _state.value
        if (s.busy) return@launch
        if (!s.order.canStart) {
            AppState.toast("Only an accepted order can be started", true)
            return@launch
        }
        updateState { it.copy(busy = true) }
        marketplaceUseCase.startOrder(orderId) { r ->
            r.handelState(
                onSuccess = {
                    AppState.toast("Job started. The customer has been notified.")
                    updateState { it.copy(busy = false) }
                    load()
                },
                onError = { error, _ ->
                    AppState.toast(error.ifBlank { "Couldn't start the job" }, true)
                    updateState { it.copy(busy = false) }
                    load()
                }
            )
        }
    }
}
