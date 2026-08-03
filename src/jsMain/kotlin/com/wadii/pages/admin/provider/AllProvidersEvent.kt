package com.wadii.pages.admin.provider

sealed class AllProvidersEvent {
    data object Load : AllProvidersEvent()
}
