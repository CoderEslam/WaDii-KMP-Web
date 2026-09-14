package com.wadii.pages.admin.provider

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.BackButton
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.GhostButton
import com.wadii.ui.InputField
import com.wadii.ui.PrimaryButton
import com.wadii.ui.SecondaryButton
import com.wadii.ui.SelectField
import org.jetbrains.compose.web.dom.*

class AddProviderScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<AddProviderViewModel>()
        val s by model.state.collectAsState()

        LaunchedEffect(s.savedSuccessfully) {
            if (s.savedSuccessfully) navigator.pop()
        }

        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                BackButton { navigator.pop() }
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Add Provider") }
            }

            s.error?.let { Alert(variant = AlertVariant.Danger, body = it) }

            Card(classes = "p-6") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Account") }
                Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                    InputField("First Name", s.firstName, required = true) { model.onEvent(AddProviderEvent.SetFirstName(it)) }
                    InputField("Last Name", s.lastName, required = true) { model.onEvent(AddProviderEvent.SetLastName(it)) }
                    InputField("Email", s.email, type = "email", required = true) { model.onEvent(AddProviderEvent.SetEmail(it)) }
                    InputField("Password", s.password, type = "password", required = true) { model.onEvent(AddProviderEvent.SetPassword(it)) }
                    InputField("Phone", s.phone, required = true) { model.onEvent(AddProviderEvent.SetPhone(it)) }
                    if (s.cities.isNotEmpty()) {
                        SelectField(
                            label = "City",
                            options = s.cities,
                            selected = s.cities.find { c -> c.id == s.cityId } ?: s.cities.first(),
                            optionLabel = { it.name }
                        ) { model.onEvent(AddProviderEvent.SetCity(it)) }
                    }
                }
            }

            Card(classes = "p-6") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Overview") }
                Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                    InputField("Provider Name", s.providerName, required = true) { model.onEvent(AddProviderEvent.SetProviderName(it)) }
                    InputField("Rate", s.rate, type = "number") { model.onEvent(AddProviderEvent.SetRate(it)) }
                }
            }

            Card(classes = "p-6") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Services") }
                if (s.allServices.isEmpty()) {
                    P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("No services available yet.") }
                } else {
                    Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                        s.allServices.forEach { svc ->
                            val selected = svc.id in s.selectedServiceIds
                            Span(attrs = {
                                style { property("cursor", "pointer") }
                                onClick { model.onEvent(AddProviderEvent.ToggleService(svc.id)) }
                            }) {
                                Badge(svc.name, variant = if (selected) BadgeVariant.Brand else BadgeVariant.Alternative, pill = true)
                            }
                        }
                    }
                }
            }

            Card(classes = "p-6") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Branches") }
                Div(attrs = { classes("space-y-4") }) {
                    s.branches.forEachIndexed { branchIndex, branch ->
                        Div(attrs = { classes("border", "border-default", "rounded-neu-base", "p-4", "space-y-3") }) {
                            Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                                InputField("Branch Name", branch.name) {
                                    model.onEvent(AddProviderEvent.SetBranchName(branchIndex, it))
                                }
                                InputField("Address", branch.address) {
                                    model.onEvent(AddProviderEvent.SetBranchAddress(branchIndex, it))
                                }
                            }
                            Div(attrs = { classes("space-y-2") }) {
                                branch.workTimes.forEachIndexed { wtIndex, wt ->
                                    Div(attrs = { classes("flex", "items-end", "gap-2") }) {
                                        P(attrs = { classes("text-sm", "font-medium", "text-heading", "w-24") }) { Text(wt.day) }
                                        InputField("Open", wt.startTime, type = "time") {
                                            model.onEvent(AddProviderEvent.SetWorkTimeStart(branchIndex, wtIndex, it))
                                        }
                                        InputField("Close", wt.closeTime, type = "time") {
                                            model.onEvent(AddProviderEvent.SetWorkTimeClose(branchIndex, wtIndex, it))
                                        }
                                    }
                                }
                            }
                            GhostButton("🗑️ Remove branch") { model.onEvent(AddProviderEvent.RemoveBranch(branchIndex)) }
                        }
                    }
                    SecondaryButton("+ Add branch", fullWidth = true) { model.onEvent(AddProviderEvent.AddBranch) }
                }
            }

            Card(classes = "p-6") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Links") }
                Div(attrs = { classes("space-y-3") }) {
                    s.links.forEachIndexed { linkIndex, link ->
                        Div(attrs = { classes("flex", "items-end", "gap-2") }) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField("URL", link, type = "url", placeholder = "https://") {
                                    model.onEvent(AddProviderEvent.SetLink(linkIndex, it))
                                }
                            }
                            GhostButton("🗑️") { model.onEvent(AddProviderEvent.RemoveLink(linkIndex)) }
                        }
                    }
                    SecondaryButton("+ Add link", fullWidth = true) { model.onEvent(AddProviderEvent.AddLink) }
                }
            }

            Div(attrs = { classes("flex", "gap-3") }) {
                SecondaryButton("Cancel", fullWidth = true) { navigator.pop() }
                PrimaryButton("Create Provider", loading = s.isSaving, fullWidth = true) { model.onEvent(AddProviderEvent.Submit) }
            }
        }
    }
}
