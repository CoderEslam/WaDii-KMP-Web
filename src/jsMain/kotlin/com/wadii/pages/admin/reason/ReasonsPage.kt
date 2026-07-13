package com.wadii.pages.admin.reason

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
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
import org.jetbrains.compose.web.dom.*

class ReasonsScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<ReasonsViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = {
                classes(
                    "text-2xl",
                    "font-semibold",
                    "text-heading"
                )
            }) { Text("Cancel Reasons") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    Card(classes = "p-5") {
                        H2(attrs = {
                            classes(
                                "font-semibold",
                                "text-heading",
                                "mb-3"
                            )
                        }) { Text("Add Reason") }
                        Div(attrs = { classes("flex", "gap-3", "items-end") }) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField(
                                    "Reason",
                                    state.newReason,
                                    required = true
                                ) { model.onEvent(ReasonsEvent.SetNewReason(it)) }
                            }
                            PrimaryButton("Add", loading = state.adding) {
                                model.onEvent(
                                    ReasonsEvent.Add
                                )
                            }
                        }
                    }

                    if (state.reasons.isEmpty()) {
                        EmptyState("🏷", "No reasons yet.")
                    } else {
                        Card {
                            Div(attrs = { classes("divide-y", "divide-default") }) {
                                state.reasons.forEach { reason ->
                                    Div(attrs = {
                                        classes(
                                            "flex",
                                            "items-center",
                                            "gap-3",
                                            "px-5",
                                            "py-4"
                                        )
                                    }) {
                                        if (state.editingId == reason.id) {
                                            Div(attrs = { classes("flex-1") }) {
                                                InputField("Reason", state.editReason) {
                                                    model.onEvent(
                                                        ReasonsEvent.SetEditReason(it)
                                                    )
                                                }
                                            }
                                            PrimaryButton(
                                                "Save",
                                                loading = state.saving
                                            ) { model.onEvent(ReasonsEvent.SaveEdit(reason)) }
                                            SecondaryButton("Cancel") { model.onEvent(ReasonsEvent.CancelEdit) }
                                        } else {
                                            Div(attrs = {
                                                classes(
                                                    "flex",
                                                    "items-center",
                                                    "gap-3",
                                                    "flex-1"
                                                )
                                            }) {
                                                IconShape(
                                                    "🏷",
                                                    size = IconShapeSize.SM,
                                                    variant = IconShapeVariant.Brand
                                                )
                                                P(attrs = {
                                                    classes(
                                                        "font-medium",
                                                        "text-heading"
                                                    )
                                                }) { Text(reason.reason) }
                                            }
                                            GhostButton("✏️") {
                                                model.onEvent(
                                                    ReasonsEvent.StartEdit(
                                                        reason
                                                    )
                                                )
                                            }
                                            GhostButton("🗑️") {
                                                model.onEvent(
                                                    ReasonsEvent.Delete(
                                                        reason.id
                                                    )
                                                )
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
    }
}
