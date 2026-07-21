package com.teacheronline.pages.admin.centers

import com.teacheronline.domain.model.EducationalCenter

sealed class CentersEvent {
    object Load : CentersEvent()
    data class SetName(val value: String) : CentersEvent()
    data class SetAddress(val value: String) : CentersEvent()
    data class SetContactInfo(val value: String) : CentersEvent()
    data class StartEdit(val center: EducationalCenter) : CentersEvent()
    object CancelEdit : CentersEvent()
    object Save : CentersEvent()
    data class Delete(val id: Long) : CentersEvent()
}
