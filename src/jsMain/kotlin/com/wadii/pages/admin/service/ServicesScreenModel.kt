package com.wadii.pages.admin.service

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.api.apiDeleteService
import com.wadii.data.api.apiInsertService
import com.wadii.data.api.apiUpdateService
import com.wadii.domain.model.service.Service
import com.wadii.state.AppState
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServicesViewModel(
    private val servicesUseCase: ServicesUseCase
) : BaseViewModel<ServicesState, ServicesEvent>() {

    override val initialState: ServicesState get() = ServicesState()

    override val state: StateFlow<ServicesState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ServicesEvent) {
        when (event) {
            ServicesEvent.Load -> load()
            is ServicesEvent.StartEdit -> updateState {
                it.copy(
                    editingId = event.service.id,
                    editName = event.service.name
                )
            }

            ServicesEvent.CancelEdit -> updateState { it.copy(editingId = 0, editName = "") }
            is ServicesEvent.SetEditName -> updateState { it.copy(editName = event.name) }
            is ServicesEvent.SaveEdit -> saveEdit(event.service)
            is ServicesEvent.Delete -> delete(event.serviceId)
            is ServicesEvent.SetNewName -> updateState { it.copy(newName = event.name) }
            ServicesEvent.Add -> add()
        }
    }

    private fun load() = screenModelScope.launch {
        servicesUseCase.getServiceList { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            services = data.data,
                            isLoading = false
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun saveEdit(service: Service) {
        val current = _state.value
        if (current.saving) return
        updateState { it.copy(saving = true) }
        screenModelScope.launch {
            val result = apiUpdateService(service.id, current.editName)
            updateState { it.copy(saving = false) }
            if (result != null) {
                updateState { it.copy(editingId = 0) }
                AppState.toast("Updated!")
                load()
            } else {
                AppState.toast("Failed to update", true)
            }
        }
    }

    private fun delete(id: Long) = screenModelScope.launch {
        if (apiDeleteService(id)) {
            AppState.toast("Deleted"); load()
        } else AppState.toast("Failed to delete", true)
    }

    private fun add() {
        val current = _state.value
        if (current.adding || current.newName.isBlank()) return
        updateState { it.copy(adding = true) }
        screenModelScope.launch {
            val result = apiInsertService(current.newName)
            updateState { it.copy(adding = false) }
            if (result != null) {
                updateState { it.copy(newName = "") }
                AppState.toast("Service added!")
                load()
            } else {
                AppState.toast("Failed to add", true)
            }
        }
    }
}
