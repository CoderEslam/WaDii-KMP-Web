package com.teacheronline.pages.teacher.secretaries

sealed class TeacherSecretariesEvent {
    object Load : TeacherSecretariesEvent()
    data class SetFirstName(val value: String) : TeacherSecretariesEvent()
    data class SetLastName(val value: String) : TeacherSecretariesEvent()
    data class SetEmail(val value: String) : TeacherSecretariesEvent()
    data class SetPassword(val value: String) : TeacherSecretariesEvent()
    data class SetPhone(val value: String) : TeacherSecretariesEvent()
    object Create : TeacherSecretariesEvent()
}
