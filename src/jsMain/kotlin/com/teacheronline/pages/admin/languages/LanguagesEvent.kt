package com.teacheronline.pages.admin.languages

import com.teacheronline.domain.model.Language

sealed class LanguagesEvent {
    object Load : LanguagesEvent()
    data class SetName(val value: String) : LanguagesEvent()
    data class SetPrefix(val value: String) : LanguagesEvent()
    data class StartEdit(val language: Language) : LanguagesEvent()
    object CancelEdit : LanguagesEvent()
    object Save : LanguagesEvent()
    data class Delete(val id: Long) : LanguagesEvent()
}
