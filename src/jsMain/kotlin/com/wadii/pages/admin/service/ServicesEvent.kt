package com.wadii.pages.admin.service

import com.wadii.domain.model.service.Service


sealed class ServicesEvent {
    object Load : ServicesEvent()
    data class StartEdit(val service: Service) : ServicesEvent()
    object CancelEdit : ServicesEvent()
    data class SetEditName(val name: String) : ServicesEvent()
    data class SaveEdit(val service: Service) : ServicesEvent()
    data class Delete(val serviceId: Int) : ServicesEvent()
    data class SetNewName(val name: String) : ServicesEvent()
    object Add : ServicesEvent()
}
