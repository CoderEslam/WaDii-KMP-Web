package com.wadii.pages.shared.profile

import org.w3c.files.File

sealed class RequestProviderEvent {
    object Load : RequestProviderEvent()

    data class SetName(val value: String) : RequestProviderEvent()
    data class SetAddress(val value: String) : RequestProviderEvent()
    data class SetPhoneNumber(val value: String) : RequestProviderEvent()

    data class ToggleService(val serviceId: Long) : RequestProviderEvent()

    object AddLink : RequestProviderEvent()
    data class RemoveLink(val linkIndex: Int) : RequestProviderEvent()
    data class SetLink(val linkIndex: Int, val value: String) : RequestProviderEvent()

    data class SetFrontIdImage(val file: File) : RequestProviderEvent()
    data class SetBackIdImage(val file: File) : RequestProviderEvent()
    data class SetTaxCardFront(val file: File) : RequestProviderEvent()
    data class SetTaxCardBack(val file: File) : RequestProviderEvent()

    object Submit : RequestProviderEvent()
}
