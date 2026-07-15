package com.teacheronline.pages.admin.students

sealed class AdminStudentsEvent {
    object Load : AdminStudentsEvent()
    data class SetFirstName(val value: String) : AdminStudentsEvent()
    data class SetLastName(val value: String) : AdminStudentsEvent()
    data class SetEmail(val value: String) : AdminStudentsEvent()
    data class SetPassword(val value: String) : AdminStudentsEvent()
    data class SetPhone(val value: String) : AdminStudentsEvent()
    data class SetParentId(val value: String) : AdminStudentsEvent()
    data class SetParentContact(val value: String) : AdminStudentsEvent()
    data class SetLevel(val id: Long) : AdminStudentsEvent()
    data class ToggleSubject(val id: Long) : AdminStudentsEvent()
    object Create : AdminStudentsEvent()
    data class Delete(val id: Long) : AdminStudentsEvent()
}
