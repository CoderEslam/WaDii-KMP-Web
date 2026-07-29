package com.wadii.pages.shared.profile

import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.province.Province
import com.wadii.domain.model.provider.EditableBranch
import com.wadii.domain.model.provider.EditableLink
import com.wadii.domain.model.provider.OfferRequest
import com.wadii.domain.model.service.Service

data class EditProfileState(
    val loading: Boolean = false,
    val saving: Boolean = false,
    val error: String? = null,
    val savedSuccessfully: Boolean = false,

    val originalUser: User? = null,
    val role: String = "",

    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",

    val countries: List<Country> = emptyList(),
    val provinces: List<Province> = emptyList(),
    val cities: List<City> = emptyList(),
    val selectedCountry: Long = 0,
    val selectedProvince: Long = 0,
    val selectedCity: Long = 0,

    val shopName: String = "",

    val allServices: List<Service> = emptyList(),
    val selectedServiceIds: Set<Int> = emptySet(),

    val branches: List<EditableBranch> = emptyList(),
    val links: List<EditableLink> = emptyList(),
    val offersPassthrough: List<OfferRequest> = emptyList()
)
