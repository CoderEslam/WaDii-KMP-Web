package com.wadii.pages.admin.provider

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.domain.model.provider.ProviderModel
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
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf

class EditProviderScreen(private val provider: ProviderModel) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<EditProviderViewModel> { parametersOf(provider) }
        val s by model.state.collectAsState()

        LaunchedEffect(s.savedSuccessfully) {
            if (s.savedSuccessfully) navigator.pop()
        }

        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                BackButton { navigator.pop() }
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Edit ${s.name}") }
            }

            s.error?.let { Alert(variant = AlertVariant.Danger, body = it) }

            Card(classes = "p-6") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Overview") }
                Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                    InputField("Rate", s.rate, type = "number") { model.onEvent(EditProviderEvent.SetRate(it)) }
                    InputField("Followers Count", s.followersCount, type = "number") {
                        model.onEvent(EditProviderEvent.SetFollowersCount(it))
                    }
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
                                onClick { model.onEvent(EditProviderEvent.ToggleService(svc.id)) }
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
                                    model.onEvent(EditProviderEvent.SetBranchName(branchIndex, it))
                                }
                                InputField("Address", branch.address) {
                                    model.onEvent(EditProviderEvent.SetBranchAddress(branchIndex, it))
                                }
                            }
                            Div(attrs = { classes("space-y-2") }) {
                                branch.workTimes.forEachIndexed { wtIndex, wt ->
                                    Div(attrs = { classes("flex", "items-end", "gap-2") }) {
                                        P(attrs = { classes("text-sm", "font-medium", "text-heading", "w-24") }) { Text(wt.day) }
                                        InputField("Open", wt.startTime, type = "time") {
                                            model.onEvent(EditProviderEvent.SetWorkTimeStart(branchIndex, wtIndex, it))
                                        }
                                        InputField("Close", wt.closeTime, type = "time") {
                                            model.onEvent(EditProviderEvent.SetWorkTimeClose(branchIndex, wtIndex, it))
                                        }
                                    }
                                }
                            }
                            GhostButton("🗑️ Remove branch") { model.onEvent(EditProviderEvent.RemoveBranch(branchIndex)) }
                        }
                    }
                    SecondaryButton("+ Add branch", fullWidth = true) { model.onEvent(EditProviderEvent.AddBranch) }
                }
            }

            Card(classes = "p-6") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Links") }
                Div(attrs = { classes("space-y-3") }) {
                    s.links.forEachIndexed { linkIndex, link ->
                        Div(attrs = { classes("flex", "items-end", "gap-2") }) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField("URL", link.link, type = "url", placeholder = "https://") {
                                    model.onEvent(EditProviderEvent.SetLink(linkIndex, it))
                                }
                            }
                            GhostButton("🗑️") { model.onEvent(EditProviderEvent.RemoveLink(linkIndex)) }
                        }
                    }
                    SecondaryButton("+ Add link", fullWidth = true) { model.onEvent(EditProviderEvent.AddLink) }
                }
            }

            Div(attrs = { classes("flex", "gap-3") }) {
                SecondaryButton("Cancel", fullWidth = true) { navigator.pop() }
                PrimaryButton("Save Changes", loading = s.isSaving, fullWidth = true) { model.onEvent(EditProviderEvent.Submit) }
            }
        }
    }
}
