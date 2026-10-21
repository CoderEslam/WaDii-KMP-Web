package com.wadii.pages.admin.provider

sealed class AllProvidersEvent {
    data object Load : AllProvidersEvent()
    /** Admin suspension: a suspended provider gets no new orders (POST /providers/set-active). */
    data class SetActive(val providerId: Long, val active: Boolean) : AllProvidersEvent()
}
