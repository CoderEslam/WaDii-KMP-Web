package com.wadii.screens.search

import com.wadii.domain.model.serach.SearchModel


data class SearchState(
    val query: String = "",
    val results: SearchModel = SearchModel(),
    val searching: Boolean = false
)
