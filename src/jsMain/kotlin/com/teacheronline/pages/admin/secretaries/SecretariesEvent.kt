package com.teacheronline.pages.admin.secretaries

sealed class SecretariesEvent {
    object Load : SecretariesEvent()
    data class SetFirstName(val value: String) : SecretariesEvent()
    data class SetLastName(val value: String) : SecretariesEvent()
    data class SetEmail(val value: String) : SecretariesEvent()
    data class SetPassword(val value: String) : SecretariesEvent()
    data class SetPhone(val value: String) : SecretariesEvent()
    data class SetTeacher(val id: Long) : SecretariesEvent()
    object Create : SecretariesEvent()
}
