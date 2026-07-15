package com.teacheronline.pages.admin.languages

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

class LanguagesScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<LanguagesViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Languages") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    Card(classes = "p-5") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) {
                            Text(if (state.editingId != 0L) "Edit Language" else "Add Language")
                        }
                        Div(attrs = { classes("flex", "gap-3", "items-end") }) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField("Name", state.name, required = true) { model.onEvent(LanguagesEvent.SetName(it)) }
                            }
                            Div(attrs = { classes("w-32") }) {
                                InputField("Prefix", state.prefix, placeholder = "en") { model.onEvent(LanguagesEvent.SetPrefix(it)) }
                            }
                            PrimaryButton(if (state.editingId != 0L) "Save" else "Add", loading = state.saving) {
                                model.onEvent(LanguagesEvent.Save)
                            }
                            if (state.editingId != 0L) {
                                SecondaryButton("Cancel") { model.onEvent(LanguagesEvent.CancelEdit) }
                            }
                        }
                    }

                    if (state.languages.isEmpty()) {
                        EmptyState("🌐", "No languages yet.")
                    } else {
                        Card {
                            Div(attrs = { classes("divide-y", "divide-default") }) {
                                state.languages.forEach { language ->
                                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                        IconShape("🌐", size = IconShapeSize.SM, variant = IconShapeVariant.Brand)
                                        Div(attrs = { classes("flex-1") }) {
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(language.name) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text(language.prefix) }
                                        }
                                        GhostButton("✏️") { model.onEvent(LanguagesEvent.StartEdit(language)) }
                                        GhostButton("🗑️") { model.onEvent(LanguagesEvent.Delete(language.id)) }
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
