package com.teacheronline.pages.admin.secretaries

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Avatar
import com.teacheronline.ui.Card
import com.teacheronline.ui.EmptyState
import com.teacheronline.ui.InputField
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.PrimaryButton
import com.teacheronline.ui.SelectField
import org.jetbrains.compose.web.dom.*

class SecretariesScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<SecretariesViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Secretaries") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    Card(classes = "p-5") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Add Secretary") }
                        Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                            InputField("First name", state.firstName, required = true) { model.onEvent(SecretariesEvent.SetFirstName(it)) }
                            InputField("Last name", state.lastName, required = true) { model.onEvent(SecretariesEvent.SetLastName(it)) }
                            InputField("Email", state.email, type = "email", required = true) { model.onEvent(SecretariesEvent.SetEmail(it)) }
                            InputField("Password", state.password, type = "password", required = true) { model.onEvent(SecretariesEvent.SetPassword(it)) }
                            InputField("Phone", state.phone) { model.onEvent(SecretariesEvent.SetPhone(it)) }
                            if (state.teachers.isNotEmpty()) {
                                SelectField(
                                    "Assigned teacher",
                                    options = state.teachers,
                                    selected = state.teachers.find { it.id == state.teacherId } ?: state.teachers.first(),
                                    optionLabel = { it.user.fullName }
                                ) { model.onEvent(SecretariesEvent.SetTeacher(it.id)) }
                            }
                        }
                        Div(attrs = { classes("mt-4") }) {
                            PrimaryButton("Create Secretary", loading = state.saving) { model.onEvent(SecretariesEvent.Create) }
                        }
                    }

                    if (state.secretaries.isEmpty()) {
                        EmptyState("🗂️", "No secretaries yet.")
                    } else {
                        Card {
                            Div(attrs = { classes("divide-y", "divide-default") }) {
                                state.secretaries.forEach { secretary ->
                                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                        Avatar(initials = secretary.user.firstName.take(1).uppercase())
                                        Div(attrs = { classes("flex-1") }) {
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(secretary.user.fullName) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) {
                                                Text("${secretary.user.email} · assists ${secretary.teacher.user.fullName}")
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
