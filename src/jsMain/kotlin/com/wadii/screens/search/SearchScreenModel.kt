package com.wadii.screens.search

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.SearchUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(private val searchUseCase: SearchUseCase) : BaseViewModel<SearchState, SearchEvent>() {

    override val initialState: SearchState
        get() = SearchState()

    override val state: StateFlow<SearchState> = _state
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.SetQuery -> updateState { it.copy(query = event.value) }
            SearchEvent.Search -> search()
        }
    }

    private fun search() = screenModelScope.launch {
        val current = _state.value
        if (current.query.isBlank() || current.searching) return@launch
        updateState { it.copy(searching = true) }
        searchUseCase.search(current.query) { response ->
            response.handelState(
                onLoading = {},
                onSuccess = { data -> updateState { it.copy(results = data.data, searching = false) } },
                onError = { _, _ -> updateState { it.copy(searching = false) } }
            )
        }
    }
}
