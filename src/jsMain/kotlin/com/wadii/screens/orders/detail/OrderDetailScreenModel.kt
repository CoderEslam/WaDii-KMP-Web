package com.wadii.screens.orders.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.rate.RateRequest
import com.wadii.domain.model.response.OrderResponse
import com.wadii.domain.model.response.ResponseDecision
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.domain.usecase.ResponseUseCase
import com.wadii.screens.orders.canComplete
import com.wadii.state.AppState
import com.wadii.utils.RequestState
import com.wadii.utils.RequestValidators
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    private val orderId: Int,
    private val orderUseCase: OrderUseCase,
    private val responseUseCase: ResponseUseCase,
    private val marketplaceUseCase: MarketplaceUseCase
) : BaseViewModel<OrderDetailState, OrderDetailEvent>() {

    override val initialState: OrderDetailState
        get() = OrderDetailState()

    override val state: StateFlow<OrderDetailState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: OrderDetailEvent) {
        when (event) {
            is OrderDetailEvent.Load -> load()
            is OrderDetailEvent.AcceptResponse -> decide(event.responseId, accept = true)
            is OrderDetailEvent.DeclineResponse -> decide(event.responseId, accept = false)
            OrderDetailEvent.Complete -> complete()
            is OrderDetailEvent.SetRating -> updateState { it.copy(stars = event.stars, ratingErrors = it.ratingErrors - "rate") }
            is OrderDetailEvent.SetRatingComment -> updateState { it.copy(ratingComment = event.value, ratingErrors = it.ratingErrors - "comment") }
            OrderDetailEvent.SubmitRating -> submitRating()
        }
    }

    private fun load() = screenModelScope.launch {
        orderUseCase.getOrderById(orderId) { response ->
            response.handelState(
                onLoading = { updateState { it.copy(isLoading = it.order == null) } },
                onSuccess = { data -> updateState { it.copy(order = data.data, isLoading = false, error = null) } },
                onError = { error, _ -> updateState { it.copy(error = error, isLoading = false) } }
            )
        }
    }

    /** Accept or decline a seller's proposal, then reload so the order shows its new state. */
    private fun decide(responseId: Long, accept: Boolean) = screenModelScope.launch {
        if (_state.value.decidingResponseId != null) return@launch
        updateState { it.copy(decidingResponseId = responseId) }

        val request = ResponseDecision(responseId)
        val handle: (RequestState<BaseResponse<OrderResponse>>) -> Unit = { result ->
            result.handelState(
                onSuccess = {
                    AppState.toast(if (accept) "Deal accepted! You can now chat with the seller." else "Proposal declined.")
                    updateState { it.copy(decidingResponseId = null) }
                    load()
                },
                onError = { error, _ ->
                    AppState.toast(
                        if (error.isNotBlank()) "Couldn't ${if (accept) "accept" else "decline"} the proposal: $error"
                        else "Something went wrong. Please try again.",
                        true
                    )
                    updateState { it.copy(decidingResponseId = null) }
                    load() // the order may have changed under us (409), so show what the server says now
                }
            )
        }
        if (accept) responseUseCase.acceptResponse(request, handle) else responseUseCase.rejectResponse(request, handle)
    }

    /** POST /orders/complete/{id}: the customer closes the job, which also unlocks rating. */
    private fun complete() = screenModelScope.launch {
        val order = _state.value.order ?: return@launch
        if (_state.value.completing) return@launch
        if (!order.canComplete) {
            AppState.toast("Only an accepted or in-progress order can be completed", true)
            return@launch
        }
        updateState { it.copy(completing = true) }
        marketplaceUseCase.completeOrder(orderId) { r ->
            r.handelState(
                onSuccess = {
                    AppState.toast("Order completed. How did it go?")
                    updateState { it.copy(completing = false) }
                    load()
                },
                onError = { error, _ ->
                    AppState.toast(error.ifBlank { "Couldn't complete the order" }, true)
                    updateState { it.copy(completing = false) }
                    load()
                }
            )
        }
    }

    /** POST /rate/insert: one rating per completed order. */
    private fun submitRating() = screenModelScope.launch {
        val s = _state.value
        if (s.ratingSubmitting) return@launch
        val errors = RequestValidators.rate(s.stars.toDouble(), s.ratingComment, orderId = orderId.toLong())
        updateState { it.copy(ratingErrors = errors) }
        if (errors.isNotEmpty()) return@launch
        updateState { it.copy(ratingSubmitting = true) }
        marketplaceUseCase.insertRate(
            RateRequest(orderId = orderId.toLong(), rate = s.stars.toDouble(), comment = s.ratingComment.trim())
        ) { r ->
            r.handelState(
                onSuccess = {
                    updateState { it.copy(ratingSubmitting = false, rated = true) }
                    AppState.toast("Thanks for your rating!")
                },
                onError = { error, _ ->
                    // 409 "You already rated this order" means the form is no longer needed.
                    val already = error.contains("already rated", ignoreCase = true)
                    updateState { it.copy(ratingSubmitting = false, rated = already) }
                    AppState.toast(error.ifBlank { "Couldn't save your rating." }, !already)
                }
            )
        }
    }
}
