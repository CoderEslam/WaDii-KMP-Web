package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiDeleteService
import com.wadii.api.apiGetAllServices
import com.wadii.api.apiInsertService
import com.wadii.api.apiUpdateService
import com.wadii.model.Service
import com.wadii.state.AppState
import kotlinx.coroutines.launch

sealed class ServicesEvent {
    object Load : ServicesEvent()
    data class StartEdit(val service: Service) : ServicesEvent()
    object CancelEdit : ServicesEvent()
    data class SetEditName(val name: String) : ServicesEvent()
    data class SaveEdit(val service: Service) : ServicesEvent()
    data class Delete(val serviceId: Long) : ServicesEvent()
    data class SetNewName(val name: String) : ServicesEvent()
    object Add : ServicesEvent()
}

data class ServicesData(
    val services: List<Service> = emptyList(),
    val editingId: Long? = null,
    val editName: String = "",
    val newName: String = "",
    val saving: Boolean = false,
    val adding: Boolean = false
)

class ServicesScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<ServicesData>>(UiState.Loading)
        private set

    init { onEvent(ServicesEvent.Load) }

    fun onEvent(event: ServicesEvent) = when (event) {
        ServicesEvent.Load -> load()
        is ServicesEvent.StartEdit -> mutate { copy(editingId = event.service.id, editName = event.service.name) }
        ServicesEvent.CancelEdit -> mutate { copy(editingId = null, editName = "") }
        is ServicesEvent.SetEditName -> mutate { copy(editName = event.name) }
        is ServicesEvent.SaveEdit -> saveEdit(event.service)
        is ServicesEvent.Delete -> delete(event.serviceId)
        is ServicesEvent.SetNewName -> mutate { copy(newName = event.name) }
        ServicesEvent.Add -> add()
    }

    private fun load() {
        screenModelScope.launch {
            val services = apiGetAllServices()
            val cur = (state as? UiState.Success)?.data
            state = UiState.Success((cur ?: ServicesData()).copy(services = services))
        }
    }

    private fun saveEdit(service: Service) {
        val d = (state as? UiState.Success)?.data ?: return
        if (d.saving) return
        mutate { copy(saving = true) }
        screenModelScope.launch {
            val result = apiUpdateService(service.id, d.editName)
            mutate { copy(saving = false) }
            if (result != null) { mutate { copy(editingId = null) }; AppState.toast("Updated!"); load() }
            else AppState.toast("Failed to update", true)
        }
    }

    private fun delete(id: Long) {
        screenModelScope.launch {
            if (apiDeleteService(id)) { AppState.toast("Deleted"); load() }
            else AppState.toast("Failed to delete", true)
        }
    }

    private fun add() {
        val d = (state as? UiState.Success)?.data ?: return
        if (d.adding || d.newName.isBlank()) return
        mutate { copy(adding = true) }
        screenModelScope.launch {
            val result = apiInsertService(d.newName)
            mutate { copy(adding = false) }
            if (result != null) { mutate { copy(newName = "") }; AppState.toast("Service added!"); load() }
            else AppState.toast("Failed to add", true)
        }
    }

    private fun mutate(block: ServicesData.() -> ServicesData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
