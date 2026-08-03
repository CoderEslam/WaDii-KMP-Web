package com.wadii.pages.admin.provider

sealed class EditProviderEvent {
    data class SetRate(val value: String) : EditProviderEvent()
    data class SetFollowersCount(val value: String) : EditProviderEvent()

    data class ToggleService(val serviceId: Long) : EditProviderEvent()

    data object AddBranch : EditProviderEvent()
    data class RemoveBranch(val branchIndex: Int) : EditProviderEvent()
    data class SetBranchName(val branchIndex: Int, val value: String) : EditProviderEvent()
    data class SetBranchAddress(val branchIndex: Int, val value: String) : EditProviderEvent()
    data class SetWorkTimeStart(val branchIndex: Int, val workTimeIndex: Int, val value: String) : EditProviderEvent()
    data class SetWorkTimeClose(val branchIndex: Int, val workTimeIndex: Int, val value: String) : EditProviderEvent()

    data object AddLink : EditProviderEvent()
    data class RemoveLink(val linkIndex: Int) : EditProviderEvent()
    data class SetLink(val linkIndex: Int, val value: String) : EditProviderEvent()

    data object Submit : EditProviderEvent()
}
