package com.wadii.pages.admin.provider

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.ProviderUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AllProvidersViewModel(
    private val providerUseCase: ProviderUseCase
) : BaseViewModel<AllProvidersState, AllProvidersEvent>() {

    override val initialState: AllProvidersState get() = AllProvidersState()

    override val state: StateFlow<AllProvidersState> = _state
        .onStart { load() }
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    override fun onEvent(event: AllProvidersEvent) {
        when (event) {
            AllProvidersEvent.Load -> load()
        }
    }

    private fun load() = screenModelScope.launch {
        providerUseCase.providersList { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            providers = data.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }
}
