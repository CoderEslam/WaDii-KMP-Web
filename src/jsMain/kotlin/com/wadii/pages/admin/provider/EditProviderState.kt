package com.wadii.pages.admin.provider

import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.provider.EditableBranch
import com.wadii.domain.model.provider.EditableLink
import com.wadii.domain.model.provider.OfferRequest
import com.wadii.domain.model.service.Service

data class EditProviderState(
    val id: Long = 0,
    val userId: Long = 0,
    val name: String = "",
    val rate: String = "0",
    val followersCount: String = "0",
    val allServices: List<Service> = emptyList(),
    val selectedServiceIds: Set<Long> = emptySet(),
    val branches: List<EditableBranch> = emptyList(),
    val links: List<EditableLink> = emptyList(),
    val user: User = User(),
    val offersPassthrough: List<OfferRequest> = emptyList(),
    val isLoadingServices: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val savedSuccessfully: Boolean = false
)
