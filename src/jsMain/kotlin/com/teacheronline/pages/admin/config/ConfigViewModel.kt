package com.teacheronline.pages.admin.config

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.usecase.ConfigUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfigViewModel(
    private val useCase: ConfigUseCase
) : BaseViewModel<ConfigState, ConfigEvent>() {

    override val initialState: ConfigState get() = ConfigState()

    override val state: StateFlow<ConfigState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: ConfigEvent) {
        when (event) {
            is ConfigEvent.SetKey -> updateState { it.copy(key = event.value) }
            ConfigEvent.Load -> load()
            is ConfigEvent.SetNewValue -> updateState { it.copy(newValue = event.value) }
            ConfigEvent.Save -> save()
        }
    }

    private fun load() = screenModelScope.launch {
        useCase.get(_state.value.key) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState { it.copy(currentValue = data.data?.value, newValue = data.data?.value?.toString() ?: "", isLoading = false) }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun save() = screenModelScope.launch {
        val s = _state.value
        useCase.put(s.key, s.newValue) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = {
                    updateState { it.copy(saving = false) }
                    AppState.toast("Config updated")
                    load()
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Save failed", true) }
            )
        }
    }
}
