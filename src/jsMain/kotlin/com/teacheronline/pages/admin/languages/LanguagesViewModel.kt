package com.teacheronline.pages.admin.languages

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.LanguageDto
import com.teacheronline.domain.usecase.LanguageUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LanguagesViewModel(
    private val useCase: LanguageUseCase
) : BaseViewModel<LanguagesState, LanguagesEvent>() {

    override val initialState: LanguagesState get() = LanguagesState()

    override val state: StateFlow<LanguagesState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: LanguagesEvent) {
        when (event) {
            LanguagesEvent.Load -> load()
            is LanguagesEvent.SetName -> updateState { it.copy(name = event.value) }
            is LanguagesEvent.SetPrefix -> updateState { it.copy(prefix = event.value) }
            is LanguagesEvent.StartEdit -> updateState {
                it.copy(editingId = event.language.id, name = event.language.name, prefix = event.language.prefix)
            }
            LanguagesEvent.CancelEdit -> updateState { it.copy(editingId = 0, name = "", prefix = "") }
            LanguagesEvent.Save -> save()
            is LanguagesEvent.Delete -> delete(event.id)
        }
    }

    private fun load() = screenModelScope.launch {
        useCase.showAll { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(languages = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun save() = screenModelScope.launch {
        val s = _state.value
        val dto = LanguageDto(id = s.editingId.takeIf { it != 0L }, name = s.name, prefix = s.prefix)
        val onResult: (com.teacheronline.utils.RequestState<com.teacheronline.domain.model.BaseResponse<com.teacheronline.domain.model.Language>>) -> Unit = { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, editingId = 0, name = "", prefix = "") }
                    AppState.toast(if (s.editingId != 0L) "Language updated" else "Language added")
                    load()
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Save failed", true) }
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
