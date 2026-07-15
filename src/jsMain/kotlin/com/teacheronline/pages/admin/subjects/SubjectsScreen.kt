package com.teacheronline.pages.admin.subjects

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

class SubjectsScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<SubjectsViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Subjects") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    Card(classes = "p-5") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) {
                            Text(if (state.editingId != 0L) "Edit Subject" else "Add Subject")
                        }
                        Div(attrs = { classes("flex", "gap-3", "items-end") }) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField("Subject name", state.name, required = true) { model.onEvent(SubjectsEvent.SetName(it)) }
                            }
                            Div(attrs = { classes("w-32") }) {
                                InputField("Price", state.price, type = "number") { model.onEvent(SubjectsEvent.SetPrice(it)) }
                            }
                            PrimaryButton(if (state.editingId != 0L) "Save" else "Add", loading = state.saving) {
                                model.onEvent(SubjectsEvent.Save)
                            }
                            if (state.editingId != 0L) {
                                SecondaryButton("Cancel") { model.onEvent(SubjectsEvent.CancelEdit) }
                            }
                        }
                    }

                    if (state.subjects.isEmpty()) {
                        EmptyState("📚", "No subjects yet.")
                    } else {
                        Card {
                            Div(attrs = { classes("divide-y", "divide-default") }) {
                                state.subjects.forEach { subject ->
                                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                        IconShape("📚", size = IconShapeSize.SM, variant = IconShapeVariant.Brand)
                                        Div(attrs = { classes("flex-1") }) {
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(subject.name) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("$${subject.price}") }
                                        }
                                        GhostButton("✏️") { model.onEvent(SubjectsEvent.StartEdit(subject)) }
                                        GhostButton("🗑️") { model.onEvent(SubjectsEvent.Delete(subject.id)) }
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
