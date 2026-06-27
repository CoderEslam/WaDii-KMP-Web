package com.wadii.pages.admin.service

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiDeleteService
import com.wadii.data.api.apiGetAllServices
import com.wadii.data.api.apiInsertService
import com.wadii.data.api.apiUpdateService
import com.wadii.domain.model.service.Service
import com.wadii.state.AppState
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class ServicesScreenModel : ScreenModel {

    var state by mutableStateOf<UiState<ServicesState>>(UiState.Loading)
        private set

    init {
        onEvent(ServicesEvent.Load)
    }

    fun onEvent(event: ServicesEvent) = when (event) {
        ServicesEvent.Load -> load()
        is ServicesEvent.StartEdit -> mutate { copy(editingId = event.service.id, editName = event.service.name) }
        ServicesEvent.CancelEdit -> mutate { copy(editingId = 0, editName = "") }
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
            state = UiState.Success((cur ?: ServicesState()).copy(services = services))
        }
    }

    private fun saveEdit(service: Service) {
        val d = (state as? UiState.Success)?.data ?: return
        if (d.saving) return
        mutate { copy(saving = true) }
        screenModelScope.launch {
            val result = apiUpdateService(service.id.toLong(), d.editName)
            mutate { copy(saving = false) }
            if (result != null) { mutate { copy(editingId = 0) }; AppState.toast("Updated!"); load() }
            else AppState.toast("Failed to update", true)
        }
    }

    private fun delete(id: Int) {
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

    private fun mutate(block: ServicesState.() -> ServicesState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
