package com.teacheronline.pages.admin.students

sealed class AdminStudentsEvent {
    object Load : AdminStudentsEvent()
    data class SetEnrollMode(val mode: EnrollMode) : AdminStudentsEvent()
    data class SetSelectedParent(val id: Long) : AdminStudentsEvent()
    data class SetParentFirstName(val value: String) : AdminStudentsEvent()
    data class SetParentLastName(val value: String) : AdminStudentsEvent()
    data class SetParentEmail(val value: String) : AdminStudentsEvent()
    data class SetParentPassword(val value: String) : AdminStudentsEvent()
    data class SetParentPhone(val value: String) : AdminStudentsEvent()
    data class SetParentContact(val value: String) : AdminStudentsEvent()
    data class SetFirstName(val value: String) : AdminStudentsEvent()
    data class SetLastName(val value: String) : AdminStudentsEvent()
    data class SetEmail(val value: String) : AdminStudentsEvent()
    data class SetPassword(val value: String) : AdminStudentsEvent()
    data class SetPhone(val value: String) : AdminStudentsEvent()
    data class SetLevel(val id: Long) : AdminStudentsEvent()
    data class ToggleSubject(val id: Long) : AdminStudentsEvent()
    object Enroll : AdminStudentsEvent()
    data class Delete(val id: Long) : AdminStudentsEvent()
}
