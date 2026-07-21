package com.wadii.pages.shared.profile

sealed class EditProfileEvent {
    object Load : EditProfileEvent()

    data class SetFirstName(val value: String) : EditProfileEvent()
    data class SetLastName(val value: String) : EditProfileEvent()
    data class SetEmail(val value: String) : EditProfileEvent()
    data class SetPhone(val value: String) : EditProfileEvent()

    data class SelectCountry(val id: Long) : EditProfileEvent()
    data class SelectProvince(val id: Long) : EditProfileEvent()
    data class SelectCity(val id: Long) : EditProfileEvent()

    data class ToggleService(val serviceId: Int) : EditProfileEvent()

    object AddBranch : EditProfileEvent()
    data class RemoveBranch(val branchIndex: Int) : EditProfileEvent()
    data class SetBranchName(val branchIndex: Int, val value: String) : EditProfileEvent()
    data class SetBranchAddress(val branchIndex: Int, val value: String) : EditProfileEvent()
    data class SetWorkTimeStart(val branchIndex: Int, val workTimeIndex: Int, val value: String) : EditProfileEvent()
    data class SetWorkTimeClose(val branchIndex: Int, val workTimeIndex: Int, val value: String) : EditProfileEvent()

    object AddLink : EditProfileEvent()
    data class RemoveLink(val linkIndex: Int) : EditProfileEvent()
    data class SetLink(val linkIndex: Int, val value: String) : EditProfileEvent()

    object Submit : EditProfileEvent()
}
