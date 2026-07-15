package com.teacheronline.pages.admin.levels

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.LevelDto
import com.teacheronline.domain.usecase.LevelUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LevelsViewModel(
    private val useCase: LevelUseCase
) : BaseViewModel<LevelsState, LevelsEvent>() {

    override val initialState: LevelsState get() = LevelsState()

    override val state: StateFlow<LevelsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: LevelsEvent) {
        when (event) {
            LevelsEvent.Load -> load()
            is LevelsEvent.SetName -> updateState { it.copy(name = event.value) }
            is LevelsEvent.StartEdit -> updateState { it.copy(editingId = event.level.id, name = event.level.name) }
            LevelsEvent.CancelEdit -> updateState { it.copy(editingId = 0, name = "") }
            LevelsEvent.Save -> save()
        }
    }

    private fun load() = screenModelScope.launch {
        useCase.all { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data -> updateState { it.copy(levels = data.data ?: emptyList(), isLoading = false) } },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun save() = screenModelScope.launch {
        val s = _state.value
        val dto = LevelDto(id = s.editingId.takeIf { it != 0L }, name = s.name)
        val onResult: (com.teacheronline.utils.RequestState<com.teacheronline.domain.model.BaseResponse<com.teacheronline.domain.model.Level>>) -> Unit = { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false, editingId = 0, name = "") }
                    AppState.toast(if (s.editingId != 0L) "Level updated" else "Level added")
                    load()
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Save failed", true) }
            )
        }
        if (s.editingId != 0L) useCase.update(dto, onResult) else useCase.create(dto, onResult)
    }
}
