package com.wadii.screens.orders.new

import com.wadii.domain.model.service.Service

data class NewOrderState(
    val services: List<Service> = emptyList(),
    val carModelYear: String = "",
    val comment: String = "",
    val selectedServices: Set<Long> = emptySet(),
    val spareParts: List<String> = listOf(""),
    val submitting: Boolean = false,
    val submitted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
