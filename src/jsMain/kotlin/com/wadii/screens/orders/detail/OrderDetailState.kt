package com.wadii.screens.orders.detail

import com.wadii.domain.model.order.OrderModel

data class OrderDetailState(
    val order: OrderModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    /** Response currently being accepted/declined, so its buttons can show a spinner. */
    val decidingResponseId: Long? = null,
    /** True while POST /orders/complete/{id} is running. */
    val completing: Boolean = false,
    // rating form: POST /rate/insert needs this (completed) order's id
    val stars: Int = 0,
    val ratingComment: String = "",
    val ratingErrors: Map<String, String> = emptyMap(),
    val ratingSubmitting: Boolean = false,
    val rated: Boolean = false
)
