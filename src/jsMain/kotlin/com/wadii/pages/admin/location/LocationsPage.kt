package com.wadii.pages.admin.location

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.province.Province
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.IconShape
import com.wadii.ui.IconShapeSize
import com.wadii.ui.IconShapeVariant
import com.wadii.ui.InputField
import com.wadii.ui.LoadingScreen
import com.wadii.ui.PrimaryButton
import com.wadii.ui.SecondaryButton
import com.wadii.ui.SelectField
import com.wadii.ui.TabVariant
import com.wadii.ui.Tabs
import org.jetbrains.compose.web.dom.*

class LocationsScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<LocationsViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Locations") }

            Tabs(
                tabs = listOf("Countries", "Provinces", "Cities"),
                icons = listOf("🌍", "🗺️", "🏙️"),
                selected = state.tab,
                variant = TabVariant.Pills,
                onSelect = { model.onEvent(LocationsEvent.SetTab(it)) }
            )

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> when (state.tab) {
                    0 -> CountriesTab(state, model)
                    1 -> ProvincesTab(state, model)
                    else -> CitiesTab(state, model)
                }
            }
        }
    }
}

@Composable
private fun CountriesTab(state: LocationsState, model: LocationsViewModel) {
    Card(classes = "p-5") {
        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Add Country") }
        Div(attrs = { classes("flex", "gap-3", "items-end") }) {
            Div(attrs = { classes("flex-1") }) {
                InputField("Name", state.newCountryName, required = true) {
                    model.onEvent(LocationsEvent.SetNewCountryName(it))
                }
            }
            PrimaryButton("Add", loading = state.addingCountry) { model.onEvent(LocationsEvent.AddCountry) }
        }
    }

    if (state.countries.isEmpty()) {
        EmptyState("🌍", "No countries yet.")
    } else {
        Card {
            Div(attrs = { classes("divide-y", "divide-default") }) {
                state.countries.forEach { country ->
                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                        if (state.editingCountryId == country.id) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField("Name", state.editCountryName) {
                                    model.onEvent(LocationsEvent.SetEditCountryName(it))
                                }
                            }
                            PrimaryButton("Save", loading = state.savingCountry) {
                                model.onEvent(LocationsEvent.SaveCountry(country))
                            }
                            SecondaryButton("Cancel") { model.onEvent(LocationsEvent.CancelEditCountry) }
                        } else {
                            Div(attrs = { classes("flex", "items-center", "gap-3", "flex-1") }) {
                                IconShape("🌍", size = IconShapeSize.SM, variant = IconShapeVariant.Brand)
                                P(attrs = { classes("font-medium", "text-heading") }) { Text(country.name) }
                            }
                            GhostButton("✏️") { model.onEvent(LocationsEvent.StartEditCountry(country)) }
                            GhostButton("🗑️") { model.onEvent(LocationsEvent.DeleteCountry(country.id)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProvincesTab(state: LocationsState, model: LocationsViewModel) {
    if (state.countries.isEmpty()) {
        EmptyState("🌍", "Add a country first before creating provinces.")
        return
    }

    Card(classes = "p-5") {
        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Add Province") }
        Div(attrs = { classes("flex", "gap-3", "items-end") }) {
            Div(attrs = { classes("flex-1") }) {
                InputField("Name", state.newProvinceName, required = true) {
                    model.onEvent(LocationsEvent.SetNewProvinceName(it))
                }
            }
            Div(attrs = { classes("flex-1") }) {
                SelectField(
                    label = "Country",
                    options = listOf(Country()) + state.countries,
                    selected = state.newProvinceCountry,
                    optionLabel = { if (it.id == 0L) "Select a country" else it.name },
                    onSelect = { model.onEvent(LocationsEvent.SetNewProvinceCountry(it)) }
                )
            }
            PrimaryButton("Add", loading = state.addingProvince) { model.onEvent(LocationsEvent.AddProvince) }
        }
    }

    if (state.provinces.isEmpty()) {
        EmptyState("🗺️", "No provinces yet.")
    } else {
        Card {
            Div(attrs = { classes("divide-y", "divide-default") }) {
                state.provinces.forEach { province ->
                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                        if (state.editingProvinceId == province.id) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField("Name", state.editProvinceName) {
                                    model.onEvent(LocationsEvent.SetEditProvinceName(it))
                                }
                            }
                            Div(attrs = { classes("flex-1") }) {
                                SelectField(
                                    label = "Country",
                                    options = listOf(Country()) + state.countries,
                                    selected = state.editProvinceCountry,
                                    optionLabel = { if (it.id == 0L) "Select a country" else it.name },
                                    onSelect = { model.onEvent(LocationsEvent.SetEditProvinceCountry(it)) }
                                )
                            }
                            PrimaryButton("Save", loading = state.savingProvince) {
                                model.onEvent(LocationsEvent.SaveProvince(province))
                            }
                            SecondaryButton("Cancel") { model.onEvent(LocationsEvent.CancelEditProvince) }
                        } else {
                            Div(attrs = { classes("flex", "items-center", "gap-3", "flex-1") }) {
                                IconShape("🗺️", size = IconShapeSize.SM, variant = IconShapeVariant.Brand)
                                Div {
                                    P(attrs = { classes("font-medium", "text-heading") }) { Text(province.name) }
                                    P(attrs = { classes("text-xs", "text-body-subtle") }) { Text(province.country.name) }
                                }
                            }
                            GhostButton("✏️") { model.onEvent(LocationsEvent.StartEditProvince(province)) }
                            GhostButton("🗑️") { model.onEvent(LocationsEvent.DeleteProvince(province.id)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CitiesTab(state: LocationsState, model: LocationsViewModel) {
    if (state.provinces.isEmpty()) {
        EmptyState("🗺️", "Add a province first before creating cities.")
        return
    }

    Card(classes = "p-5") {
        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Add City") }
        Div(attrs = { classes("flex", "gap-3", "items-end") }) {
            Div(attrs = { classes("flex-1") }) {
                InputField("Name", state.newCityName, required = true) {
                    model.onEvent(LocationsEvent.SetNewCityName(it))
                }
            }
            Div(attrs = { classes("flex-1") }) {
                SelectField(
                    label = "Province",
                    options = listOf(Province()) + state.provinces,
                    selected = state.newCityProvince,
                    optionLabel = { if (it.id == 0L) "Select a province" else "${it.name} (${it.country.name})" },
                    onSelect = { model.onEvent(LocationsEvent.SetNewCityProvince(it)) }
                )
            }
            PrimaryButton("Add", loading = state.addingCity) { model.onEvent(LocationsEvent.AddCity) }
        }
    }

    if (state.cities.isEmpty()) {
        EmptyState("🏙️", "No cities yet.")
    } else {
        Card {
            Div(attrs = { classes("divide-y", "divide-default") }) {
                state.cities.forEach { city ->
                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                        if (state.editingCityId == city.id) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField("Name", state.editCityName) {
                                    model.onEvent(LocationsEvent.SetEditCityName(it))
                                }
                            }
                            Div(attrs = { classes("flex-1") }) {
                                SelectField(
                                    label = "Province",
                                    options = listOf(Province()) + state.provinces,
                                    selected = state.editCityProvince,
                                    optionLabel = { if (it.id == 0L) "Select a province" else "${it.name} (${it.country.name})" },
                                    onSelect = { model.onEvent(LocationsEvent.SetEditCityProvince(it)) }
                                )
                            }
                            PrimaryButton("Save", loading = state.savingCity) {
                                model.onEvent(LocationsEvent.SaveCity(city))
                            }
                            SecondaryButton("Cancel") { model.onEvent(LocationsEvent.CancelEditCity) }
                        } else {
                            Div(attrs = { classes("flex", "items-center", "gap-3", "flex-1") }) {
                                IconShape("🏙️", size = IconShapeSize.SM, variant = IconShapeVariant.Brand)
                                Div {
                                    P(attrs = { classes("font-medium", "text-heading") }) { Text(city.name) }
                                    P(attrs = { classes("text-xs", "text-body-subtle") }) {
                                        Text("${city.province.name}, ${city.province.country.name}")
                                    }
                                }
                            }
                            GhostButton("✏️") { model.onEvent(LocationsEvent.StartEditCity(city)) }
                            GhostButton("🗑️") { model.onEvent(LocationsEvent.DeleteCity(city.id)) }
                        }
                    }
                }
            }
        }
    }
}
