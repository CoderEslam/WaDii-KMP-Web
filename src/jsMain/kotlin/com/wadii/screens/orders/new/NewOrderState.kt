package com.wadii.screens.orders.new

import com.wadii.domain.model.service.Service

data class NewOrderState(
    val services: List<Service> = emptyList(),
    val carModelYear: String = "",
    /** Make comes from GET /car-types/show-all; details is the free-text model + year. carModelYear is the two joined. */
    val carMake: String = "",
    val carDetails: String = "",
    val carTypes: List<com.wadii.domain.model.carTypes.CarType> = emptyList(),
    val comment: String = "",
    /** yyyy-MM-dd the customer needs the job done by; the server rejects dates before yesterday. */
    val date: String = com.wadii.utils.todayIso(),
    val selectedServices: Set<Long> = emptySet(),
    val spareParts: List<String> = listOf(""),
    val submitting: Boolean = false,
    val submitted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    /** Per-field messages keyed by field (car, services, parts). */
    val fieldErrors: Map<String, String> = emptyMap(),
    /** Admin-managed common parts, offered as one-tap suggestions. */
    val suggestions: List<com.wadii.domain.model.shop.CommonSparePart> = emptyList()
)
