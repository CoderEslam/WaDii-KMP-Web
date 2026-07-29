package com.wadii.pages.admin.reason

import com.wadii.domain.model.order.CancelReason


sealed class ReasonsEvent {
    object Load : ReasonsEvent()
    data class StartEdit(val reason: CancelReason) : ReasonsEvent()
    object CancelEdit : ReasonsEvent()
    data class SetEditReason(val reason: String) : ReasonsEvent()
    data class SaveEdit(val reason: CancelReason) : ReasonsEvent()
    data class Delete(val reasonId: Int) : ReasonsEvent()
    data class SetNewReason(val reason: String) : ReasonsEvent()
    object Add : ReasonsEvent()
}
