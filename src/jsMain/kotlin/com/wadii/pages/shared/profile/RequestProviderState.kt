package com.wadii.pages.shared.profile

import com.wadii.domain.model.service.Service
import org.w3c.files.File

data class RequestProviderState(
    val loading: Boolean = false,
    val submitting: Boolean = false,
    val error: String? = null,
    val submittedSuccessfully: Boolean = false,

    val name: String = "",
    val address: String = "",
    val phoneNumber: String = "",

    val allServices: List<Service> = emptyList(),
    val selectedServiceIds: Set<Long> = emptySet(),

    val links: List<String> = emptyList(),

    val frontIdImage: File? = null,
    val backIdImage: File? = null,
    val taxCardFront: File? = null,
    val taxCardBack: File? = null
)
