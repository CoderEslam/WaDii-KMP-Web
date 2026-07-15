package com.teacheronline.pages.admin.centers

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.EducationalCenterDto
import com.teacheronline.domain.usecase.EducationalCenterUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CentersViewModel(
    private val useCase: EducationalCenterUseCase
) : BaseViewModel<CentersState, CentersEvent>() {

    override val initialState: CentersState get() = CentersState()

    override val state: StateFlow<CentersState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: CentersEvent) {
        when (event) {
            CentersEvent.Load -> load()
            is CentersEvent.SetName -> updateState { it.copy(name = event.value) }
            is CentersEvent.SetAddress -> updateState { it.copy(address = event.value) }
            is CentersEvent.SetContactInfo -> updateState { it.copy(contactInfo = event.value) }
            is CentersEvent.StartEdit -> updateState {
                it.copy(editingId = event.center.id, name = event.center.name, address = event.center.address, contactInfo = event.center.contactInfo)
            }
            CentersEvent.CancelEdit -> updateState { it.copy(editingId = 0, name = "", address = "", contactInfo = "") }
            CentersEvent.Save -> save()
            is CentersEvent.Delete -> delete(event.id)
        }
    }

    private fun load() = screenModelScope.launch {
        useCase.showAll { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(centers = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun save() = screenModelScope.launch {
        val s = _state.value
        val dto = EducationalCenterDto(
            id = s.editingId.takeIf { it != 0L },
            name = s.name,
            address = s.address,
            contactInfo = s.contactInfo
        )
        val onResult: (com.teacheronline.utils.RequestState<com.teacheronline.domain.model.BaseResponse<com.teacheronline.domain.model.EducationalCenter>>) -> Unit = { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, editingId = 0, name = "", address = "", contactInfo = "") }
                    AppState.toast(if (s.editingId != 0L) "Center updated" else "Center created")
                    load()
                },
                onError = { e, _ ->
                    updateState { it.copy(saving = false, error = e) }
                    AppState.toast("Save failed", true)
                }
            )
        }
        if (s.editingId != 0L) useCase.update(dto, onResult) else useCase.insert(dto, onResult)
    }

    private fun delete(id: Long) = screenModelScope.launch {
        useCase.delete(id) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { updateState { it.copy(isLoading = false) }; AppState.toast("Deleted"); load() },
                onError = { e, _ -> updateState { it.copy(isLoading = false, error = e) }; AppState.toast("Delete failed", true) }
            )
        }
    }
}
