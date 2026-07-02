package com.wadii.pages.shared.profile

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
import com.wadii.ui.LoadingScreen
import com.wadii.ui.PrimaryButton
import com.wadii.ui.SecondaryButton
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

class EditProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<EditProfileViewModel>()
        val s by model.state.collectAsState()

        LaunchedEffect(s.savedSuccessfully) {
            if (s.savedSuccessfully) navigator.pop()
        }

        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                BackButton { navigator.pop() }
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Edit Profile") }
            }

            if (s.loading) {
                LoadingScreen()
            } else {
                s.error?.let { Alert(variant = AlertVariant.Danger, body = it) }

                Card(classes = "p-6") {
                    H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Personal Info") }
                    Div(attrs = { classes("space-y-4") }) {
                        Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                            InputField("First Name", s.firstName, required = true) { model.onEvent(EditProfileEvent.SetFirstName(it)) }
                            InputField("Last Name", s.lastName, required = true) { model.onEvent(EditProfileEvent.SetLastName(it)) }
                        }
                        InputField("Email", s.email, type = "email", required = true) { model.onEvent(EditProfileEvent.SetEmail(it)) }
                        InputField("Phone", s.phone, type = "tel") { model.onEvent(EditProfileEvent.SetPhone(it)) }

                        if (s.role == "PROVIDER") {
                            Div(attrs = { classes("space-y-1") }) {
                                P(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text("Shop Name") }
                                Div(attrs = {
                                    classes(
                                        "w-full", "px-4", "py-2.5", "border", "border-default-medium", "rounded-neu-base",
                                        "bg-surface-secondary", "text-sm", "text-body-subtle"
                                    )
                                }) { Text(s.shopName) }
                                P(attrs = { classes("text-xs", "text-body-subtle") }) {
                                    Text("Shop name can't be changed here — contact support")
                                }
                            }
                        }

                        Div(attrs = { classes("space-y-3") }) {
                            P(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text("Location") }
                            SelectField("Country", s.countries.map { it.id to it.name }, s.selectedCountry) {
                                model.onEvent(EditProfileEvent.SelectCountry(it))
                            }
                            if (s.provinces.isNotEmpty())
                                SelectField("Province", s.provinces.map { it.id to it.name }, s.selectedProvince) {
                                    model.onEvent(EditProfileEvent.SelectProvince(it))
                                }
                            if (s.cities.isNotEmpty())
                                SelectField("City", s.cities.map { it.id to it.name }, s.selectedCity) {
                                    model.onEvent(EditProfileEvent.SelectCity(it))
                                }
                        }
                    }
                }

                if (s.role == "PROVIDER") {
                    Card(classes = "p-6") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Services") }
                        if (s.allServices.isEmpty()) {
                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("No services available yet.") }
                        } else {
                            Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                                s.allServices.forEach { svc ->
                                    val selected = svc.id.toInt() in s.selectedServiceIds
                                    Span(attrs = {
                                        style { property("cursor", "pointer") }
                                        onClick { model.onEvent(EditProfileEvent.ToggleService(svc.id.toInt())) }
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
                                            model.onEvent(EditProfileEvent.SetBranchName(branchIndex, it))
                                        }
                                        InputField("Address", branch.address) {
                                            model.onEvent(EditProfileEvent.SetBranchAddress(branchIndex, it))
                                        }
                                    }
                                    Div(attrs = { classes("space-y-2") }) {
                                        branch.workTimes.forEachIndexed { wtIndex, wt ->
                                            Div(attrs = { classes("flex", "items-end", "gap-2") }) {
                                                InputField("Day", wt.day, placeholder = "Monday") {
                                                    model.onEvent(EditProfileEvent.SetWorkTimeDay(branchIndex, wtIndex, it))
                                                }
                                                InputField("Open", wt.startTime, type = "time") {
                                                    model.onEvent(EditProfileEvent.SetWorkTimeStart(branchIndex, wtIndex, it))
                                                }
                                                InputField("Close", wt.closeTime, type = "time") {
                                                    model.onEvent(EditProfileEvent.SetWorkTimeClose(branchIndex, wtIndex, it))
                                                }
                                                GhostButton("🗑️") {
                                                    model.onEvent(EditProfileEvent.RemoveWorkTime(branchIndex, wtIndex))
                                                }
                                            }
                                        }
                                        SecondaryButton("+ Add work time") { model.onEvent(EditProfileEvent.AddWorkTime(branchIndex)) }
                                    }
                                    GhostButton("🗑️ Remove branch") { model.onEvent(EditProfileEvent.RemoveBranch(branchIndex)) }
                                }
                            }
                            SecondaryButton("+ Add branch", fullWidth = true) { model.onEvent(EditProfileEvent.AddBranch) }
                        }
                    }

                    Card(classes = "p-6") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Links") }
                        Div(attrs = { classes("space-y-3") }) {
                            s.links.forEachIndexed { linkIndex, link ->
                                Div(attrs = { classes("flex", "items-end", "gap-2") }) {
                                    Div(attrs = { classes("flex-1") }) {
                                        InputField("URL", link.link, type = "url", placeholder = "https://") {
                                            model.onEvent(EditProfileEvent.SetLink(linkIndex, it))
                                        }
                                    }
                                    GhostButton("🗑️") { model.onEvent(EditProfileEvent.RemoveLink(linkIndex)) }
                                }
                            }
                            SecondaryButton("+ Add link", fullWidth = true) { model.onEvent(EditProfileEvent.AddLink) }
                        }
                    }
                }

                Div(attrs = { classes("flex", "gap-3") }) {
                    SecondaryButton("Cancel", fullWidth = true) { navigator.pop() }
                    PrimaryButton("Save Changes", loading = s.saving, fullWidth = true) { model.onEvent(EditProfileEvent.Submit) }
                }
            }
        }
    }
}

@Composable
private fun SelectField(label: String, options: List<Pair<Int, String>>, selectedId: Int, onChange: (Int) -> Unit) {
    Div(attrs = { classes("flex", "flex-col", "gap-2") }) {
        Label(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text(label) }
        Select(attrs = {
            classes(
                "w-full", "px-4", "py-2.5", "border", "border-default-medium", "rounded-neu-base",
                "bg-surface", "shadow-neu-inset", "text-sm", "text-heading",
                "focus:outline-none", "focus:ring-1", "focus:ring-brand", "focus:border-brand"
            )
            onChange { event -> onChange((event.value ?: "").toIntOrNull() ?: 0) }
        }) {
            Option(value = "0", attrs = { if (selectedId == 0) selected() }) { Text("Select $label") }
            options.forEach { (id, name) ->
                Option(value = id.toString(), attrs = { if (selectedId == id) selected() }) { Text(name) }
            }
        }
    }
}
