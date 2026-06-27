package com.wadii.screens.auth.register

sealed class RegisterEvent {
    data class SetFirstName(val value: String) : RegisterEvent()
    data class SetLastName(val value: String) : RegisterEvent()
    data class SetEmail(val value: String) : RegisterEvent()
    data class SetPassword(val value: String) : RegisterEvent()
    data class SetPhone(val value: String) : RegisterEvent()
    data class SetUserType(val type: Int) : RegisterEvent()
    data class SetProviderName(val value: String) : RegisterEvent()
    data class SelectCountry(val id: Int) : RegisterEvent()
    data class SelectProvince(val id: Int) : RegisterEvent()
    data class SelectCity(val id: Int) : RegisterEvent()
    object Submit : RegisterEvent()
}
