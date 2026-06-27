package com.wadii.screens.auth.register

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.data.api.apiGetCities
import com.wadii.data.api.apiGetCountries
import com.wadii.data.api.apiGetProvinces
import com.wadii.data.api.apiRegister
import com.wadii.domain.model.auth.register.RegisterRequest
import com.wadii.state.AppState
import kotlinx.coroutines.launch

class RegisterScreenModel : ScreenModel {
    var state by mutableStateOf(RegisterState())
        private set

    init {
        screenModelScope.launch { state = state.copy(countries = apiGetCountries()) }
    }

    fun onEvent(event: RegisterEvent) = when (event) {
        is RegisterEvent.SetFirstName -> state = state.copy(firstName = event.value)
        is RegisterEvent.SetLastName -> state = state.copy(lastName = event.value)
        is RegisterEvent.SetEmail -> state = state.copy(email = event.value)
        is RegisterEvent.SetPassword -> state = state.copy(password = event.value)
        is RegisterEvent.SetPhone -> state = state.copy(phone = event.value)
        is RegisterEvent.SetUserType -> state = state.copy(userType = event.type)
        is RegisterEvent.SetProviderName -> state = state.copy(providerName = event.value)
        is RegisterEvent.SelectCountry -> loadProvinces(event.id)
        is RegisterEvent.SelectProvince -> loadCities(event.id)
        is RegisterEvent.SelectCity -> state = state.copy(selectedCity = event.id)
        RegisterEvent.Submit -> submit()
    }

    private fun loadProvinces(countryId: Int) {
        state = state.copy(selectedCountry = countryId, provinces = emptyList(), cities = emptyList(), selectedProvince = 0, selectedCity = 0)
        screenModelScope.launch { state = state.copy(provinces = apiGetProvinces(countryId)) }
    }

    private fun loadCities(provinceId: Int) {
        state = state.copy(selectedProvince = provinceId, cities = emptyList(), selectedCity = 0)
        screenModelScope.launch { state = state.copy(cities = apiGetCities(provinceId)) }
    }

    private fun submit() {
        val s = state
        if (s.loading || s.selectedCity == 0) {
            AppState.toast("Please select a city", true)
            return
        }
        state = s.copy(loading = true)
        screenModelScope.launch {
            val user = apiRegister(
                RegisterRequest(
                    email = s.email, password = s.password,
                    firstName = s.firstName, lastName = s.lastName,
                    phone = s.phone,
                    cityId = s.selectedCity.toInt(),
                    userType = s.userType,
//                    providerName = if (s.userType == 1) s.providerName else ""
                )
            )
            state = state.copy(loading = false)
            if (user != null && user.token != null) {
                AppState.login(user, user.token!!)
                AppState.toast("Account created!")
            } else {
                AppState.toast("Registration failed. Email may already be in use.", true)
            }
        }
    }
}
