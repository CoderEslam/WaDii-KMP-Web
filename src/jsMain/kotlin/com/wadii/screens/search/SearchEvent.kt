package com.wadii.screens.search

sealed class SearchEvent {
    data class SetQuery(val value: String) : SearchEvent()
    object Search : SearchEvent()
}
