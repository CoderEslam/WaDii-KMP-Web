package com.teacheronline.pages.admin.subjects

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.SubjectRequest
import com.teacheronline.domain.usecase.SubjectUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubjectsViewModel(
    private val useCase: SubjectUseCase
) : BaseViewModel<SubjectsState, SubjectsEvent>() {

    override val initialState: SubjectsState get() = SubjectsState()

    override val state: StateFlow<SubjectsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: SubjectsEvent) {
        when (event) {
            SubjectsEvent.Load -> load()
            is SubjectsEvent.SetName -> updateState { it.copy(name = event.value) }
            is SubjectsEvent.SetPrice -> updateState { it.copy(price = event.value) }
            is SubjectsEvent.StartEdit -> updateState {
                it.copy(editingId = event.subject.id, name = event.subject.name, price = event.subject.price.toString())
            }
            SubjectsEvent.CancelEdit -> updateState { it.copy(editingId = 0, name = "", price = "") }
            SubjectsEvent.Save -> save()
            is SubjectsEvent.Delete -> delete(event.id)
        }
    }

    private fun load() = screenModelScope.launch {
        useCase.showAll { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(subjects = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun save() = screenModelScope.launch {
        val s = _state.value
        val request = SubjectRequest(id = s.editingId.takeIf { it != 0L }, name = s.name, price = s.price.toDoubleOrNull() ?: 0.0)
        val onResult: (com.teacheronline.utils.RequestState<com.teacheronline.domain.model.BaseResponse<com.teacheronline.domain.model.Subject>>) -> Unit = { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, editingId = 0, name = "", price = "") }
                    AppState.toast(if (s.editingId != 0L) "Subject updated" else "Subject added")
                    load()
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Save failed", true) }
            )
        }
        if (s.editingId != 0L) useCase.update(request, onResult) else useCase.insert(request, onResult)
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
