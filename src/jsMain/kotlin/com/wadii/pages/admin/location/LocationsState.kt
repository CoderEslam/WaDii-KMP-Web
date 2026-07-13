package com.wadii.pages.admin.location

import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.province.Province

data class LocationsState(
    val tab: Int = 0,

    val countries: List<Country> = emptyList(),
    val provinces: List<Province> = emptyList(),
    val cities: List<City> = emptyList(),

    val isLoading: Boolean = false,
    val error: String? = null,

    // country form
    val newCountryName: String = "",
    val addingCountry: Boolean = false,
    val editingCountryId: Long = 0,
    val editCountryName: String = "",
    val savingCountry: Boolean = false,

    // province form
    val newProvinceName: String = "",
    val newProvinceCountry: Country = Country(),
    val addingProvince: Boolean = false,
    val editingProvinceId: Long = 0,
    val editProvinceName: String = "",
    val editProvinceCountry: Country = Country(),
    val savingProvince: Boolean = false,

    // city form
    val newCityName: String = "",
    val newCityProvince: Province = Province(),
    val addingCity: Boolean = false,
    val editingCityId: Long = 0,
    val editCityName: String = "",
    val editCityProvince: Province = Province(),
    val savingCity: Boolean = false
)
