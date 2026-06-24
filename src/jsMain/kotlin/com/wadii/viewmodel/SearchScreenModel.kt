package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiSearch
import com.wadii.model.SearchResult
import kotlinx.coroutines.launch

sealed class SearchEvent {
    data class SetQuery(val value: String) : SearchEvent()
    object Search : SearchEvent()
}

data class SearchData(
    val query: String = "",
    val results: SearchResult? = null,
    val searching: Boolean = false
)

class SearchScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<SearchData>>(UiState.Success(SearchData()))
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

    private fun mutate(block: SearchData.() -> SearchData) {
        val d = (state as? UiState.Success)?.data ?: return
        state = UiState.Success(d.block())
    }
}
