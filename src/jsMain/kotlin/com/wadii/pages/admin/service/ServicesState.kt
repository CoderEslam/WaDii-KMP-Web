package com.wadii.pages.admin.service

import com.wadii.domain.model.service.Service


data class ServicesState(
    val services: List<Service> = emptyList(),
    val editingId: Long = 0,
    val editName: String = "",
    val newName: String = "",
    val saving: Boolean = false,
    val adding: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
