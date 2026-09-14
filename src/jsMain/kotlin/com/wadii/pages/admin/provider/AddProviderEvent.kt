package com.wadii.pages.admin.provider

import com.wadii.domain.model.city.City

sealed interface AddProviderEvent {
    data class SetFirstName(val value: String) : AddProviderEvent
    data class SetLastName(val value: String) : AddProviderEvent
    data class SetEmail(val value: String) : AddProviderEvent
    data class SetPassword(val value: String) : AddProviderEvent
    data class SetPhone(val value: String) : AddProviderEvent
    data class SetCity(val city: City) : AddProviderEvent
    data class SetProviderName(val value: String) : AddProviderEvent
    data class SetRate(val value: String) : AddProviderEvent

    data class ToggleService(val serviceId: Long) : AddProviderEvent

    object AddBranch : AddProviderEvent
    data class RemoveBranch(val branchIndex: Int) : AddProviderEvent
    data class SetBranchName(val branchIndex: Int, val value: String) : AddProviderEvent
    data class SetBranchAddress(val branchIndex: Int, val value: String) : AddProviderEvent
    data class SetWorkTimeStart(val branchIndex: Int, val workTimeIndex: Int, val value: String) : AddProviderEvent
    data class SetWorkTimeClose(val branchIndex: Int, val workTimeIndex: Int, val value: String) : AddProviderEvent

    object AddLink : AddProviderEvent
    data class RemoveLink(val linkIndex: Int) : AddProviderEvent
    data class SetLink(val linkIndex: Int, val value: String) : AddProviderEvent

    object Submit : AddProviderEvent
}
