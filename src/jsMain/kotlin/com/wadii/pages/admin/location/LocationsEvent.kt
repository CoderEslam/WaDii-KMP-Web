package com.wadii.pages.admin.location

import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.province.Province

sealed class LocationsEvent {
    object Load : LocationsEvent()
    data class SetTab(val tab: Int) : LocationsEvent()

    // country
    data class SetNewCountryName(val name: String) : LocationsEvent()
    object AddCountry : LocationsEvent()
    data class StartEditCountry(val country: Country) : LocationsEvent()
    object CancelEditCountry : LocationsEvent()
    data class SetEditCountryName(val name: String) : LocationsEvent()
    data class SaveCountry(val country: Country) : LocationsEvent()
    data class DeleteCountry(val id: Long) : LocationsEvent()

    // province
    data class SetNewProvinceName(val name: String) : LocationsEvent()
    data class SetNewProvinceCountry(val country: Country) : LocationsEvent()
    object AddProvince : LocationsEvent()
    data class StartEditProvince(val province: Province) : LocationsEvent()
    object CancelEditProvince : LocationsEvent()
    data class SetEditProvinceName(val name: String) : LocationsEvent()
    data class SetEditProvinceCountry(val country: Country) : LocationsEvent()
    data class SaveProvince(val province: Province) : LocationsEvent()
    data class DeleteProvince(val id: Long) : LocationsEvent()

    // city
    data class SetNewCityName(val name: String) : LocationsEvent()
    data class SetNewCityProvince(val province: Province) : LocationsEvent()
    object AddCity : LocationsEvent()
    data class StartEditCity(val city: City) : LocationsEvent()
    object CancelEditCity : LocationsEvent()
    data class SetEditCityName(val name: String) : LocationsEvent()
    data class SetEditCityProvince(val province: Province) : LocationsEvent()
    data class SaveCity(val city: City) : LocationsEvent()
    data class DeleteCity(val id: Long) : LocationsEvent()
}
