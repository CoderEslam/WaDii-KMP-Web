package com.wadii.pages.admin.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.usecase.AdminDashboardUseCase
import com.wadii.domain.usecase.AdsUseCase
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AdminDashboardScreenModel(
    private val adminDashboardUesCase: AdminDashboardUseCase,
    private val adsUseCase: AdsUseCase,
    private val servicesUseCase: ServicesUseCase
) : BaseViewModel<AdminState, AdminDashboardEvent>() {

    override val initialState: AdminState
        get() = AdminState()


    override val state: StateFlow<AdminState> = _state
        .onStart {
            requests()
            services()
            ads()
        }.stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            AdminState()
        )


    override fun onEvent(event: AdminDashboardEvent) {
//        when (event) {
//
//        }
    }

    private fun requests() = screenModelScope.launch {
        adminDashboardUesCase.requests { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                },
                onSuccess = { data ->
                    updateState { it.copy(isLoading = false, requests = data.data) }
                }, onError = { error, code ->
                    updateState { it.copy(isLoading = false, message = error) }
                }
            )
        }
    }

    private fun services() = screenModelScope.launch {
        servicesUseCase.getServiceList { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                },
                onSuccess = { data ->
                    updateState { it.copy(isLoading = false, services = data.data) }
                }, onError = { error, code ->
                    updateState { it.copy(isLoading = false, message = error) }
                }
            )
        }
    }

    private fun ads() = screenModelScope.launch {
        adsUseCase.ads { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                },
                onSuccess = { data ->
                    updateState { it.copy(isLoading = false, ads = data.data) }
                }, onError = { error, code ->
                    updateState { it.copy(isLoading = false, message = error) }
                }
            )
        }
    }

}
