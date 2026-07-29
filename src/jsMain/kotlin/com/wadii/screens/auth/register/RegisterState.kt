package com.wadii.screens.auth.register

import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.province.Province

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val userType: Int = 0,
    val providerName: String = "",
    val countries: List<Country> = emptyList(),
    val provinces: List<Province> = emptyList(),
    val cities: List<City> = emptyList(),
    val selectedCountry: Long = 0,
    val selectedProvince: Long = 0,
    val selectedCity: Long = 0,
    val loading: Boolean = false
)
