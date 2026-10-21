package com.wadii.pages.provider.dashboard

sealed class ProviderDashboardEvent {
    object Load : ProviderDashboardEvent()
    data class SetAccepting(val accepting: Boolean) : ProviderDashboardEvent()
}
