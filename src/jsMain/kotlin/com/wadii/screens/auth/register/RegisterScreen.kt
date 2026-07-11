package com.wadii.screens.auth.register

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.ui.Card
import com.wadii.ui.InputField
import com.wadii.ui.PrimaryButton
import com.wadii.ui.TabVariant
import com.wadii.ui.Tabs
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.dom.*

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<RegisterViewModel>()
        val s by model.state.collectAsState()

        Div(attrs = {
            classes(
                "min-h-screen",
                "flex",
                "items-center",
                "justify-center",
                "p-4",
                "py-10"
            )
        }) {
            Div(attrs = { classes("w-full", "max-w-md") }) {
                Div(attrs = { classes("text-center", "mb-10") }) {
                    H1(attrs = {
                        classes(
                            "text-5xl",
                            "font-extrabold",
                            "brand-text",
                            "tracking-tight",
                            "mb-3"
                        )
                    }) { Text("WaDii") }
                    P(attrs = {
                        classes(
                            "text-body-subtle",
                            "text-sm",
                            "tracking-widest",
                            "uppercase"
                        )
                    }) { Text("Create your account") }
                }

                Card(classes = "p-8") {
                    Div(attrs = { classes("space-y-4") }) {
                        Tabs(
                            tabs = listOf("Buyer"/*, "Seller"*/),
                            selected = s.userType,
                            onSelect = { model.onEvent(RegisterEvent.SetUserType(it)) },
                            variant = TabVariant.Pills
                        )

                        Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                            InputField(
                                "First Name",
                                s.firstName,
                                "John",
                                required = true
                            ) { model.onEvent(RegisterEvent.SetFirstName(it)) }
                            InputField(
                                "Last Name",
                                s.lastName,
                                "Doe",
                                required = true
                            ) { model.onEvent(RegisterEvent.SetLastName(it)) }
                        }

                        InputField(
                            "Email",
                            s.email,
                            "you@example.com",
                            "email",
                            true
                        ) { model.onEvent(RegisterEvent.SetEmail(it)) }
                        InputField("Phone", s.phone, "+1 234 567 8900", "tel") {
                            model.onEvent(
                                RegisterEvent.SetPhone(it)
                            )
                        }
                        InputField(
                            "Password",
                            s.password,
                            "••••••••",
                            "password",
                            true
                        ) { model.onEvent(RegisterEvent.SetPassword(it)) }

                        if (s.userType == 1) {
                            InputField(
                                "Shop / Business Name",
                                s.providerName,
                                "My Auto Parts Co.",
                                required = true
                            ) { model.onEvent(RegisterEvent.SetProviderName(it)) }
                        }

                        Div(attrs = { classes("space-y-3") }) {
                            P(attrs = { classes("text-sm", "font-medium", "text-heading") }) {
                                Text(
                                    "Location"
                                )
                            }
                            SelectField(
                                "Country",
                                s.countries.map { it.id to it.name },
                                s.selectedCountry
                            ) { model.onEvent(RegisterEvent.SelectCountry(it)) }
                            if (s.provinces.isNotEmpty())
                                SelectField(
                                    "Province",
                                    s.provinces.map { it.id to it.name },
                                    s.selectedProvince
                                ) { model.onEvent(RegisterEvent.SelectProvince(it)) }
                            if (s.cities.isNotEmpty())
                                SelectField(
                                    "Area",
                                    s.cities.map { it.id to it.name },
                                    s.selectedCity
                                ) { model.onEvent(RegisterEvent.SelectCity(it)) }
                        }

                        PrimaryButton("Create Account", loading = s.loading, fullWidth = true) {
                            model.onEvent(RegisterEvent.Submit)
                        }
                    }

                    P(attrs = { classes("mt-6", "text-center", "text-sm", "text-body-subtle") }) {
                        Text("Already have an account? ")
                        Span(attrs = {
                            classes(
                                "text-fg-brand",
                                "font-semibold",
                                "cursor-pointer",
                                "hover:underline"
                            )
                            onClick { navigator.pop() }
                        }) { Text("Sign in →") }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectField(
    label: String,
    options: List<Pair<Long, String>>,
    selectedId: Long,
    onChange: (Long) -> Unit
) {
    Div(attrs = { classes("flex", "flex-col", "gap-2") }) {
        Label(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text(label) }
        Select(attrs = {
            classes(
                "w-full", "px-4", "py-2.5", "border", "border-default-medium", "rounded-neu-base",
                "bg-surface", "shadow-neu-inset", "text-sm", "text-heading",
                "focus:outline-none", "focus:ring-1", "focus:ring-brand", "focus:border-brand"
            )
            onChange { event -> onChange((event.value ?: "").toLongOrNull() ?: 0) }
        }) {
            Option(
                value = "0",
                attrs = { if (selectedId == 0L) selected() }) { Text("Select $label") }
            options.forEach { (id, name) ->
                Option(value = id.toString(), attrs = { if (selectedId == id) selected() }) {
                    Text(
                        name
                    )
                }
            }
        }
    }
}
