package com.wadii.screens.search

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiSearch
import com.wadii.viewmodel.UiState
import kotlinx.coroutines.launch

class SearchScreenModel : ScreenModel {
    var state by mutableStateOf<UiState<SearchState>>(UiState.Success(SearchState()))
        private set

    fun onEvent(event: SearchEvent) = when (event) {
        is SearchEvent.SetQuery -> mutate { copy(query = event.value) }
        SearchEvent.Search -> search()
    }

    private fun search() {
        val d = (state as? UiState.Success)?.data ?: return
        if (d.query.isBlank() || d.searching) return
        mutate { copy(searching = true) }
        screenModelScope.launch {
            val results = apiSearch(d.query)
            mutate { copy(results = results, searching = false) }
        }
    }

    private fun mutate(block: SearchState.() -> SearchState) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
