package com.wadii.pages.admin.service

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.service.InsertService
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

    private fun saveEdit(service: Service) = screenModelScope.launch {
        servicesUseCase.updateService(
            insertService = InsertService(
                id = service.id,
                name = _state.value.editName
            )
        ) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(saving = true) }
            }, onSuccess = {
                updateState { it.copy(saving = false) }
                AppState.toast("Updated!")
                load()
            }, onError = { error, code ->
                updateState { it.copy(error = error, saving = false) }
                AppState.toast("Failed to update", true)
            })
        }
    }

    private fun delete(id: Long) = screenModelScope.launch {
        servicesUseCase.deleteService(id) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(isLoading = true) }
            }, onSuccess = {
                updateState { it.copy(isLoading = false) }
                AppState.toast("Deleted"); load()
            }, onError = { error, code ->
                updateState { it.copy(error = error, isLoading = false) }
                AppState.toast("Failed to delete", true)
            })
        }
    }

    private fun add() = screenModelScope.launch {
        servicesUseCase.addService(insertService = InsertService(name = _state.value.newName)) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(isLoading = true) }
            }, onSuccess = {
                updateState { it.copy(newName = "", isLoading = false) }
                AppState.toast("Service added!")
            }, onError = { error, code ->
                updateState { it.copy(error = error, isLoading = false) }
                AppState.toast("Failed to add", true)
            })
        }
    }

}
