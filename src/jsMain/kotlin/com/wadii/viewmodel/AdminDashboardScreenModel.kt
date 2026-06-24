package com.wadii.viewmodel

import androidx.compose.runtime.*
import com.wadii.api.apiGetAllAds
import com.wadii.api.apiGetAllRequests
import com.wadii.api.apiGetAllServices
import com.wadii.model.ProviderRequest
import kotlinx.coroutines.launch

sealed class AdminDashboardEvent {
    object Load : AdminDashboardEvent()
}

data class AdminDashboardData(
    val requestCount: Int,
    val adCount: Int,
    val serviceCount: Int,
    val recentRequests: List<ProviderRequest>
)

class AdminDashboardScreenModel : ScreenModel() {
    var state by mutableStateOf<UiState<AdminDashboardData>>(UiState.Loading)
        private set

    init { onEvent(AdminDashboardEvent.Load) }

    fun onEvent(event: AdminDashboardEvent) = when (event) {
        AdminDashboardEvent.Load -> load()
    }

    private fun load() {
        screenModelScope.launch {
            state = UiState.Loading
            val requests = apiGetAllRequests()
            val ads = apiGetAllAds()
            val services = apiGetAllServices()
            state = UiState.Success(AdminDashboardData(requests.size, ads.size, services.size, requests.take(5)))
        }
    }
}
