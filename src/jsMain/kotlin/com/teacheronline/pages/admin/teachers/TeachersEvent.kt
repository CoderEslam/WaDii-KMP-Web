package com.teacheronline.pages.admin.teachers

import com.teacheronline.domain.model.Teacher

sealed class TeachersEvent {
    object Load : TeachersEvent()
    data class SetFirstName(val value: String) : TeachersEvent()
    data class SetLastName(val value: String) : TeachersEvent()
    data class SetEmail(val value: String) : TeachersEvent()
    data class SetPassword(val value: String) : TeachersEvent()
    data class SetPhone(val value: String) : TeachersEvent()
    data class ToggleSubject(val id: Long) : TeachersEvent()
    data class StartEdit(val teacher: Teacher) : TeachersEvent()
    object CancelEdit : TeachersEvent()
    object Save : TeachersEvent()
    data class Delete(val id: Long) : TeachersEvent()
}
