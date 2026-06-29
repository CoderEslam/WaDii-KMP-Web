package com.wadii.screens.auth.register

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.ui.InputField
import com.wadii.ui.Spinner
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.dom.*

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<RegisterViewModel>()
        val s by model.state.collectAsState()

        Div(attrs = { classes("min-h-screen", "flex", "items-center", "justify-center", "p-4", "py-10") }) {
            Div(attrs = { classes("w-full", "max-w-md") }) {
                Div(attrs = { classes("text-center", "mb-10") }) {
                    H1(attrs = { classes("text-5xl", "font-extrabold", "brand-text", "tracking-tight", "mb-3") }) { Text("WaDii") }
                    P(attrs = { classes("text-slate-500", "text-sm", "tracking-widest", "uppercase") }) { Text("Create your account") }
                }

                Div(attrs = { classes("bg-white", "rounded-3xl", "shadow-lg", "p-8", "border", "border-slate-200") }) {
                    Div(attrs = { classes("space-y-4") }) {
                        Div(attrs = { classes("flex", "gap-3") }) {
                            listOf("User" to 0, "Provider" to 1).forEach { (label, type) ->
                                Button(attrs = {
                                    attr("type", "button")
                                    classes("flex-1", "py-2", "rounded-xl", "text-sm", "font-medium", "transition-colors")
                                    if (s.userType == type) classes("bg-amber-500", "text-white")
                                    else classes("bg-slate-100", "text-slate-700", "hover:bg-slate-200")
                                    onClick { model.onEvent(RegisterEvent.SetUserType(type)) }
                                }) { Text(label) }
                            }
                        }

                        Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                            InputField("First Name", s.firstName, "John", required = true) { model.onEvent(RegisterEvent.SetFirstName(it)) }
                            InputField("Last Name", s.lastName, "Doe", required = true) { model.onEvent(RegisterEvent.SetLastName(it)) }
                        }

                        InputField("Email", s.email, "you@example.com", "email", true) { model.onEvent(RegisterEvent.SetEmail(it)) }
                        InputField("Phone", s.phone, "+1 234 567 8900", "tel") { model.onEvent(RegisterEvent.SetPhone(it)) }
                        InputField("Password", s.password, "••••••••", "password", true) { model.onEvent(RegisterEvent.SetPassword(it)) }

                        if (s.userType == 1) {
                            InputField("Provider / Business Name", s.providerName, "My Service Co.", required = true) { model.onEvent(RegisterEvent.SetProviderName(it)) }
                        }

                        Div(attrs = { classes("space-y-3") }) {
                            P(attrs = { classes("text-sm", "font-medium", "text-slate-700") }) { Text("Location") }
                            SelectField("Country", s.countries.map { it.id to it.name }, s.selectedCountry) { model.onEvent(RegisterEvent.SelectCountry(it)) }
                            if (s.provinces.isNotEmpty())
                                SelectField("Province", s.provinces.map { it.id to it.name }, s.selectedProvince) { model.onEvent(RegisterEvent.SelectProvince(it)) }
                            if (s.cities.isNotEmpty())
                                SelectField("City", s.cities.map { it.id to it.name }, s.selectedCity) { model.onEvent(RegisterEvent.SelectCity(it)) }
                        }

                        Button(attrs = {
                            classes("w-full", "py-3", "bg-amber-500", "hover:bg-amber-600", "text-white", "font-semibold", "rounded-xl", "transition-colors", "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2")
                            attr("type", "button")
                            onClick { model.onEvent(RegisterEvent.Submit) }
                            if (s.loading) disabled()
                        }) {
                            if (s.loading) Spinner() else Text("Create Account")
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
}

@Composable
private fun SelectField(label: String, options: List<Pair<Int, String>>, selectedId: Int, onChange: (Int) -> Unit) {
    Div(attrs = { classes("flex", "flex-col", "gap-1") }) {
        Label(attrs = { classes("text-xs", "text-slate-500") }) { Text(label) }
        Select(attrs = {
            classes("w-full", "px-3", "py-2", "border", "border-slate-300", "rounded-lg", "text-sm", "focus:outline-none", "focus:ring-2", "focus:ring-amber-400", "bg-white")
            onChange { event -> onChange((event.value ?: "").toIntOrNull() ?: 0) }
        }) {
            Option(value = "0", attrs = { if (selectedId == 0) selected() }) { Text("Select $label") }
            options.forEach { (id, name) ->
                Option(value = id.toString(), attrs = { if (selectedId == id) selected() }) { Text(name) }
            }
        }
    }
}
