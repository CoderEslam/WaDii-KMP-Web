package com.teacheronline

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Event> : ScreenModel {

    // Each ViewModel must define the initial state
    abstract val initialState: State

    // Internal mutable state flow
    protected val _state: MutableStateFlow<State> = MutableStateFlow(initialState)

    // Public (read-only) state flow
    abstract val state: StateFlow<State>


    // Helper function to update state safely
    protected fun updateState(reducer: suspend (State) -> State) {
        screenModelScope.launch {
            _state.value = reducer(_state.value)
        }
    }

    // Must be implemented by children
    abstract fun onEvent(event: Event)
}