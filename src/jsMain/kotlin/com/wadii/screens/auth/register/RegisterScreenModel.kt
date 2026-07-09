package com.wadii.screens.auth.register

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.data.firebase.FcmService
import com.wadii.domain.model.auth.register.RegisterRequest
import com.wadii.domain.usecase.AuthUseCase
import com.wadii.domain.usecase.CountryUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authUseCase: AuthUseCase,
    private val countryUseCase: CountryUseCase
) : BaseViewModel<RegisterState, RegisterEvent>() {

    override val initialState: RegisterState
        get() = RegisterState()

    override val state: StateFlow<RegisterState> = _state
        .onStart { loadCountries() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.SetFirstName -> updateState { it.copy(firstName = event.value) }
            is RegisterEvent.SetLastName -> updateState { it.copy(lastName = event.value) }
            is RegisterEvent.SetEmail -> updateState { it.copy(email = event.value) }
            is RegisterEvent.SetPassword -> updateState { it.copy(password = event.value) }
            is RegisterEvent.SetPhone -> updateState { it.copy(phone = event.value) }
            is RegisterEvent.SetUserType -> updateState { it.copy(userType = event.type) }
            is RegisterEvent.SetProviderName -> updateState { it.copy(providerName = event.value) }
            is RegisterEvent.SelectCountry -> loadProvinces(event.id)
            is RegisterEvent.SelectProvince -> loadCities(event.id)
            is RegisterEvent.SelectCity -> updateState { it.copy(selectedCity = event.id) }
            RegisterEvent.Submit -> submit()
        }
    }

    private suspend fun loadCountries() {
        countryUseCase.getCountryList { response ->
            response.handelState(
                onLoading = {},
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            countries = data.data ?: emptyList()
                        )
                    }
                },
                onError = { _, _ -> }
            )
        }
    }

    private fun loadProvinces(countryId: Long) {
        updateState {
            it.copy(
                selectedCountry = countryId,
                provinces = emptyList(),
                cities = emptyList(),
                selectedProvince = 0,
                selectedCity = 0
            )
        }
        screenModelScope.launch {
            countryUseCase.getProvinceByCountryId(countryId) { response ->
                response.handelState(
                    onLoading = {},
                    onSuccess = { data ->
                        updateState {
                            it.copy(
                                provinces = data.data ?: emptyList()
                            )
                        }
                    },
                    onError = { _, _ -> }
                )
            }
        }
    }

    private fun loadCities(provinceId: Long) {
        updateState {
            it.copy(
                selectedProvince = provinceId,
                cities = emptyList(),
                selectedCity = 0
            )
        }
        screenModelScope.launch {
            countryUseCase.getCitiesByProvinceId(provinceId) { response ->
                response.handelState(
                    onLoading = {},
                    onSuccess = { data ->
                        updateState {
                            it.copy(
                                cities = data.data ?: emptyList()
                            )
                        }
                    },
                    onError = { _, _ -> }
                )
            }
        }
    }

    private fun submit() = screenModelScope.launch {
        val s = _state.value
        if (s.loading || s.selectedCity == 0L) {
            AppState.toast("Please select a city", true)
            return@launch
        }
        updateState { it.copy(loading = true) }
        val fcmToken = FcmService.fetchToken().orEmpty()
        authUseCase.register(
            RegisterRequest(
                email = s.email,
                password = s.password,
                fcmToken = fcmToken,
                firstName = s.firstName,
                lastName = s.lastName,
                phone = s.phone,
                cityId = s.selectedCity,
                userType = 0 //s.userType
            )
        ) { response ->
            response.handelState(
                onLoading = {},
                onSuccess = { data ->
                    val user = data.data
                    if (user != null && user.token != null) {
                        AppState.saveUser(user, user.token!!)
                        AppState.toast("Account created!")
                    } else {
                        AppState.toast("Registration failed.", true)
                    }
                    updateState { it.copy(loading = false) }
                },
                onError = { _, _ ->
                    AppState.toast("Registration failed. Email may already be in use.", true)
                    updateState { it.copy(loading = false) }
                }
            )
        }
    }
}
