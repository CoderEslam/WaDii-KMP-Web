package com.teacheronline.pages.admin.centers

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Card
import com.teacheronline.ui.EmptyState
import com.teacheronline.ui.GhostButton
import com.teacheronline.ui.IconShape
import com.teacheronline.ui.IconShapeSize
import com.teacheronline.ui.IconShapeVariant
import com.teacheronline.ui.InputField
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.PrimaryButton
import com.teacheronline.ui.SecondaryButton
import org.jetbrains.compose.web.dom.*

class CentersScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<CentersViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Educational Centers") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    Card(classes = "p-5") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) {
                            Text(if (state.editingId != 0L) "Edit Center" else "Add Center")
                        }
                        Div(attrs = { classes("grid", "gap-3", "md:grid-cols-3") }) {
                            InputField("Name", state.name, required = true) { model.onEvent(CentersEvent.SetName(it)) }
                            InputField("Address", state.address) { model.onEvent(CentersEvent.SetAddress(it)) }
                            InputField("Contact info", state.contactInfo) { model.onEvent(CentersEvent.SetContactInfo(it)) }
                        }
                        Div(attrs = { classes("flex", "gap-3", "mt-4") }) {
                            PrimaryButton(if (state.editingId != 0L) "Save" else "Add", loading = state.saving) {
                                model.onEvent(CentersEvent.Save)
                            }
                            if (state.editingId != 0L) {
                                SecondaryButton("Cancel") { model.onEvent(CentersEvent.CancelEdit) }
                            }
                        }
                    }

                    if (state.centers.isEmpty()) {
                        EmptyState("🏫", "No educational centers yet.")
                    } else {
                        Card {
                            Div(attrs = { classes("divide-y", "divide-default") }) {
                                state.centers.forEach { center ->
                                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                        IconShape("🏫", size = IconShapeSize.SM, variant = IconShapeVariant.Brand)
                                        Div(attrs = { classes("flex-1") }) {
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(center.name) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("${center.address} · ${center.contactInfo}") }
                                        }
                                        GhostButton("✏️") { model.onEvent(CentersEvent.StartEdit(center)) }
                                        GhostButton("🗑️") { model.onEvent(CentersEvent.Delete(center.id)) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
