package com.teacheronline.pages.admin.subjects

import com.teacheronline.domain.model.Subject

sealed class SubjectsEvent {
    object Load : SubjectsEvent()
    data class SetName(val value: String) : SubjectsEvent()
    data class SetPrice(val value: String) : SubjectsEvent()
    data class StartEdit(val subject: Subject) : SubjectsEvent()
    object CancelEdit : SubjectsEvent()
    object Save : SubjectsEvent()
    data class Delete(val id: Long) : SubjectsEvent()
}
