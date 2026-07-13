package com.wadii.pages.admin.location

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.city.City
import com.wadii.domain.model.city.InsertCity
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.country.InsertCountry
import com.wadii.domain.model.province.InsertProvince
import com.wadii.domain.model.province.Province
import com.wadii.domain.usecase.CountryUseCase
import com.wadii.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocationsViewModel(
    private val countryUseCase: CountryUseCase
) : BaseViewModel<LocationsState, LocationsEvent>() {

    override val initialState: LocationsState get() = LocationsState()

    override val state: StateFlow<LocationsState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: LocationsEvent) {
        when (event) {
            LocationsEvent.Load -> load()
            is LocationsEvent.SetTab -> updateState { it.copy(tab = event.tab) }

            is LocationsEvent.SetNewCountryName -> updateState { it.copy(newCountryName = event.name) }
            LocationsEvent.AddCountry -> addCountry()
            is LocationsEvent.StartEditCountry -> updateState {
                it.copy(editingCountryId = event.country.id, editCountryName = event.country.name)
            }
            LocationsEvent.CancelEditCountry -> updateState { it.copy(editingCountryId = 0, editCountryName = "") }
            is LocationsEvent.SetEditCountryName -> updateState { it.copy(editCountryName = event.name) }
            is LocationsEvent.SaveCountry -> saveCountry(event.country)
            is LocationsEvent.DeleteCountry -> deleteCountry(event.id)

            is LocationsEvent.SetNewProvinceName -> updateState { it.copy(newProvinceName = event.name) }
            is LocationsEvent.SetNewProvinceCountry -> updateState { it.copy(newProvinceCountry = event.country) }
            LocationsEvent.AddProvince -> addProvince()
            is LocationsEvent.StartEditProvince -> updateState {
                it.copy(
                    editingProvinceId = event.province.id,
                    editProvinceName = event.province.name,
                    editProvinceCountry = event.province.country
                )
            }
            LocationsEvent.CancelEditProvince -> updateState {
                it.copy(editingProvinceId = 0, editProvinceName = "", editProvinceCountry = Country())
            }
            is LocationsEvent.SetEditProvinceName -> updateState { it.copy(editProvinceName = event.name) }
            is LocationsEvent.SetEditProvinceCountry -> updateState { it.copy(editProvinceCountry = event.country) }
            is LocationsEvent.SaveProvince -> saveProvince(event.province)
            is LocationsEvent.DeleteProvince -> deleteProvince(event.id)

            is LocationsEvent.SetNewCityName -> updateState { it.copy(newCityName = event.name) }
            is LocationsEvent.SetNewCityProvince -> updateState { it.copy(newCityProvince = event.province) }
            LocationsEvent.AddCity -> addCity()
            is LocationsEvent.StartEditCity -> updateState {
                it.copy(
                    editingCityId = event.city.id,
                    editCityName = event.city.name,
                    editCityProvince = event.city.province
                )
            }
            LocationsEvent.CancelEditCity -> updateState {
                it.copy(editingCityId = 0, editCityName = "", editCityProvince = Province())
            }
            is LocationsEvent.SetEditCityName -> updateState { it.copy(editCityName = event.name) }
            is LocationsEvent.SetEditCityProvince -> updateState { it.copy(editCityProvince = event.province) }
            is LocationsEvent.SaveCity -> saveCity(event.city)
            is LocationsEvent.DeleteCity -> deleteCity(event.id)
        }
    }

    private fun load() = screenModelScope.launch {
        updateState { it.copy(isLoading = true, error = null) }
        countryUseCase.getCountryList { r ->
            r.handelState(
                onSuccess = { data -> updateState { it.copy(countries = data.data) } },
                onError = { e, _ -> updateState { it.copy(error = e) } }
            )
        }
        countryUseCase.getProvinceList { r ->
            r.handelState(
                onSuccess = { data -> updateState { it.copy(provinces = data.data) } },
                onError = { e, _ -> updateState { it.copy(error = e) } }
            )
        }
        countryUseCase.getCityList { r ->
            r.handelState(
                onSuccess = { data -> updateState { it.copy(cities = data.data) } },
                onError = { e, _ -> updateState { it.copy(error = e) } }
            )
        }
        updateState { it.copy(isLoading = false) }
    }

    // ---- country ----

    private fun addCountry() = screenModelScope.launch {
        val name = _state.value.newCountryName.trim()
        if (name.isEmpty()) return@launch
        countryUseCase.insertCountry(InsertCountry(name = name)) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(addingCountry = true) }
            }, onSuccess = {
                updateState { it.copy(newCountryName = "", addingCountry = false) }
                AppState.toast("Country added!")
                load()
            }, onError = { error, _ ->
                updateState { it.copy(addingCountry = false) }
                AppState.toast(error.ifBlank { "Failed to add country" }, true)
            })
        }
    }

    private fun saveCountry(country: Country) = screenModelScope.launch {
        val name = _state.value.editCountryName.trim()
        if (name.isEmpty()) return@launch
        countryUseCase.updateCountry(InsertCountry(id = country.id, name = name)) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(savingCountry = true) }
            }, onSuccess = {
                updateState { it.copy(savingCountry = false, editingCountryId = 0, editCountryName = "") }
                AppState.toast("Country updated!")
                load()
            }, onError = { error, _ ->
                updateState { it.copy(savingCountry = false) }
                AppState.toast(error.ifBlank { "Failed to update country" }, true)
            })
        }
    }

    private fun deleteCountry(id: Long) = screenModelScope.launch {
        countryUseCase.deleteCountry(id) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(isLoading = true) }
            }, onSuccess = {
                updateState { it.copy(isLoading = false) }
                AppState.toast("Country deleted")
                load()
            }, onError = { error, _ ->
                updateState { it.copy(isLoading = false) }
                AppState.toast(error.ifBlank { "Failed to delete country" }, true)
            })
        }
    }

    // ---- province ----

    private fun addProvince() = screenModelScope.launch {
        val state = _state.value
        val name = state.newProvinceName.trim()
        if (name.isEmpty() || state.newProvinceCountry.id == 0L) {
            AppState.toast("Select a country and enter a name", true)
            return@launch
        }
        countryUseCase.insertProvince(InsertProvince(name = name, countryId = state.newProvinceCountry.id)) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(addingProvince = true) }
            }, onSuccess = {
                updateState { it.copy(newProvinceName = "", newProvinceCountry = Country(), addingProvince = false) }
                AppState.toast("Province added!")
                load()
            }, onError = { error, _ ->
                updateState { it.copy(addingProvince = false) }
                AppState.toast(error.ifBlank { "Failed to add province" }, true)
            })
        }
    }

    private fun saveProvince(province: Province) = screenModelScope.launch {
        val state = _state.value
        val name = state.editProvinceName.trim()
        if (name.isEmpty() || state.editProvinceCountry.id == 0L) {
            AppState.toast("Select a country and enter a name", true)
            return@launch
        }
        countryUseCase.updateProvince(
            InsertProvince(id = province.id, name = name, countryId = state.editProvinceCountry.id)
        ) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(savingProvince = true) }
            }, onSuccess = {
                updateState {
                    it.copy(
                        savingProvince = false,
                        editingProvinceId = 0,
                        editProvinceName = "",
                        editProvinceCountry = Country()
                    )
                }
                AppState.toast("Province updated!")
                load()
            }, onError = { error, _ ->
                updateState { it.copy(savingProvince = false) }
                AppState.toast(error.ifBlank { "Failed to update province" }, true)
            })
        }
    }

    private fun deleteProvince(id: Long) = screenModelScope.launch {
        countryUseCase.deleteProvince(id) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(isLoading = true) }
            }, onSuccess = {
                updateState { it.copy(isLoading = false) }
                AppState.toast("Province deleted")
                load()
            }, onError = { error, _ ->
                updateState { it.copy(isLoading = false) }
                AppState.toast(error.ifBlank { "Failed to delete province" }, true)
            })
        }
    }

    // ---- city ----

    private fun addCity() = screenModelScope.launch {
        val state = _state.value
        val name = state.newCityName.trim()
        if (name.isEmpty() || state.newCityProvince.id == 0L) {
            AppState.toast("Select a province and enter a name", true)
            return@launch
        }
        countryUseCase.insertCity(InsertCity(name = name, provinceId = state.newCityProvince.id)) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(addingCity = true) }
            }, onSuccess = {
                updateState { it.copy(newCityName = "", newCityProvince = Province(), addingCity = false) }
                AppState.toast("City added!")
                load()
            }, onError = { error, _ ->
                updateState { it.copy(addingCity = false) }
                AppState.toast(error.ifBlank { "Failed to add city" }, true)
            })
        }
    }

    private fun saveCity(city: City) = screenModelScope.launch {
        val state = _state.value
        val name = state.editCityName.trim()
        if (name.isEmpty() || state.editCityProvince.id == 0L) {
            AppState.toast("Select a province and enter a name", true)
            return@launch
        }
        countryUseCase.updateCity(
            InsertCity(id = city.id, name = name, provinceId = state.editCityProvince.id)
        ) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(savingCity = true) }
            }, onSuccess = {
                updateState {
                    it.copy(
                        savingCity = false,
                        editingCityId = 0,
                        editCityName = "",
                        editCityProvince = Province()
                    )
                }
                AppState.toast("City updated!")
                load()
            }, onError = { error, _ ->
                updateState { it.copy(savingCity = false) }
                AppState.toast(error.ifBlank { "Failed to update city" }, true)
            })
        }
    }

    private fun deleteCity(id: Long) = screenModelScope.launch {
        countryUseCase.deleteCity(id) { response ->
            response.handelState(onLoading = {
                updateState { it.copy(isLoading = true) }
            }, onSuccess = {
                updateState { it.copy(isLoading = false) }
                AppState.toast("City deleted")
                load()
            }, onError = { error, _ ->
                updateState { it.copy(isLoading = false) }
                AppState.toast(error.ifBlank { "Failed to delete city" }, true)
            })
        }
    }
}
