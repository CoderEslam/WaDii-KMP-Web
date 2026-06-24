package com.wadii.pages.auth

import androidx.compose.runtime.*
import com.wadii.api.*
import com.wadii.model.AuthRequest
import com.wadii.model.City
import com.wadii.model.Country
import com.wadii.model.Province
import com.wadii.state.AppState
import com.wadii.navigation.LocalNavigator
import com.wadii.navigation.currentOrThrow
import com.wadii.ui.InputField
import com.wadii.ui.Spinner
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.selected
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.*

@Composable
fun RegisterPage() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf(0) }
    var providerName by remember { mutableStateOf("") }

    var countries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var provinces by remember { mutableStateOf<List<Province>>(emptyList()) }
    var cities by remember { mutableStateOf<List<City>>(emptyList()) }
    var selectedCountry by remember { mutableStateOf<Long>(0) }
    var selectedProvince by remember { mutableStateOf<Long>(0) }
    var selectedCity by remember { mutableStateOf<Long>(0) }

    val navigator = LocalNavigator.currentOrThrow
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { countries = apiGetCountries() }
    LaunchedEffect(selectedCountry) {
        if (selectedCountry > 0) {
            provinces = apiGetProvinces(selectedCountry)
            cities = emptyList()
            selectedProvince = 0
            selectedCity = 0
        }
    }
    LaunchedEffect(selectedProvince) {
        if (selectedProvince > 0) {
            cities = apiGetCities(selectedProvince)
            selectedCity = 0
        }
    }

    fun submit() {
        if (loading || selectedCity == 0L) {
            AppState.toast("Please select a city", true)
            return
        }
        loading = true
        scope.launch {
            val user = apiRegister(AuthRequest(
                email = email, password = password,
                firstName = firstName, lastName = lastName,
                phone = phone, cityId = selectedCity,
                userType = userType,
                providerName = if (userType == 1) providerName else ""
            ))
            loading = false
            if (user != null && user.token != null) {
                AppState.login(user, user.token!!)
                AppState.toast("Account created!")
            } else {
                AppState.toast("Registration failed. Email may already be in use.", true)
            }
        }
    }

    Div(attrs = { classes("min-h-screen", "flex", "items-center", "justify-center", "p-4", "py-10") }) {
        Div(attrs = { classes("w-full", "max-w-md") }) {
            Div(attrs = { classes("text-center", "mb-10") }) {
                H1(attrs = { classes("text-5xl", "font-extrabold", "brand-text", "tracking-tight", "mb-3") }) { Text("WaDii") }
                P(attrs = { classes("text-slate-500", "text-sm", "tracking-widest", "uppercase") }) { Text("Create your account") }
            }

            Div(attrs = { classes("bg-white", "rounded-3xl", "shadow-lg", "p-8", "border", "border-slate-200") }) {
                Div(attrs = { classes("space-y-4") }) {
                    // Account type
                    Div(attrs = { classes("flex", "gap-3") }) {
                        listOf("User" to 0, "Provider" to 1).forEach { (label, type) ->
                            Button(attrs = {
                                attr("type", "button")
                                classes("flex-1", "py-2", "rounded-xl", "text-sm", "font-medium", "transition-colors")
                                if (userType == type) classes("bg-amber-500", "text-white") else classes("bg-slate-100", "text-slate-700", "hover:bg-slate-200")
                                onClick { userType = type }
                            }) { Text(label) }
                        }
                    }

                    Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                        InputField("First Name", firstName, "John", required = true) { firstName = it }
                        InputField("Last Name", lastName, "Doe", required = true) { lastName = it }
                    }

                    InputField("Email", email, "you@example.com", "email", true) { email = it }
                    InputField("Phone", phone, "+1 234 567 8900", "tel") { phone = it }
                    InputField("Password", password, "••••••••", "password", true) { password = it }

                    if (userType == 1) {
                        InputField("Provider / Business Name", providerName, "My Service Co.", required = true) { providerName = it }
                    }

                    // Location selectors
                    Div(attrs = { classes("space-y-3") }) {
                        P(attrs = { classes("text-sm", "font-medium", "text-slate-700") }) { Text("Location") }
                        SelectField("Country", countries.map { it.id to it.name }, selectedCountry) { selectedCountry = it }
                        if (provinces.isNotEmpty())
                            SelectField("Province", provinces.map { it.id to it.name }, selectedProvince) { selectedProvince = it }
                        if (cities.isNotEmpty())
                            SelectField("City", cities.map { it.id to it.name }, selectedCity) { selectedCity = it }
                    }

                    Button(attrs = {
                        classes("w-full", "py-3", "bg-amber-500", "hover:bg-amber-600",
                            "text-white", "font-semibold", "rounded-xl", "transition-colors",
                            "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2")
                        attr("type", "button")
                        onClick { submit() }
                        if (loading) disabled()
                    }) {
                        if (loading) Spinner() else Text("Create Account")
                    }
                }

                P(attrs = { classes("mt-6", "text-center", "text-sm", "text-slate-500") }) {
                    Text("Already have an account? ")
                    Span(attrs = {
                        classes("text-amber-500", "font-semibold", "cursor-pointer", "hover:underline")
                        onClick { navigator.pop() }
                    }) { Text("Sign in →") }
                }
            }
        }
    }
}

@Composable
fun SelectField(label: String, options: List<Pair<Long, String>>, selected: Long, onChange: (Long) -> Unit) {
    val sel = selected
    Div(attrs = { classes("flex", "flex-col", "gap-1") }) {
        Label(attrs = { classes("text-xs", "text-slate-500") }) { Text(label) }
        Select(attrs = {
            classes("w-full", "px-3", "py-2", "border", "border-slate-300", "rounded-lg",
                "text-sm", "focus:outline-none", "focus:ring-2", "focus:ring-amber-400", "bg-white")
            onChange { event -> onChange((event.value ?: "").toLongOrNull() ?: 0L) }
        }) {
            Option(value = "0", attrs = { if (sel == 0L) selected() }) { Text("Select $label") }
            options.forEach { (id, name) ->
                Option(value = id.toString(), attrs = { if (sel == id) selected() }) { Text(name) }
            }
        }
    }
}
