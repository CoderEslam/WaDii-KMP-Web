package com.wadii.pages.admin.provider

import com.wadii.domain.model.city.City
import com.wadii.domain.model.service.Service

data class AddProviderState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val cityId: Long = 0,
    val cities: List<City> = emptyList(),
    val providerName: String = "",
    val rate: String = "0",
    val allServices: List<Service> = emptyList(),
    val selectedServiceIds: Set<Long> = emptySet(),
    val branches: List<NewBranch> = emptyList(),
    val links: List<String> = emptyList(),
    val isLoadingOptions: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val savedSuccessfully: Boolean = false
)

data class NewBranch(
    val name: String = "",
    val address: String = "",
    val workTimes: List<NewWorkTime> = emptyList()
)

data class NewWorkTime(
    val day: String,
    val startTime: String = "",
    val closeTime: String = ""
)
