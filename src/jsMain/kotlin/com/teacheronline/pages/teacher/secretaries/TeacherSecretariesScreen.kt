package com.teacheronline.pages.teacher.secretaries

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
import org.jetbrains.compose.web.dom.*

class TeacherSecretariesScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<TeacherSecretariesViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("My Secretaries") }

            if (state.error != null) Alert(variant = AlertVariant.Danger, body = state.error!!)
            if (state.isLoading) LoadingScreen()

            Card(classes = "p-5") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Add Secretary") }
                Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                    InputField("First name", state.firstName, required = true) { model.onEvent(TeacherSecretariesEvent.SetFirstName(it)) }
                    InputField("Last name", state.lastName, required = true) { model.onEvent(TeacherSecretariesEvent.SetLastName(it)) }
                    InputField("Email", state.email, type = "email", required = true) { model.onEvent(TeacherSecretariesEvent.SetEmail(it)) }
                    InputField("Password", state.password, type = "password", required = true) { model.onEvent(TeacherSecretariesEvent.SetPassword(it)) }
                    InputField("Phone", state.phone) { model.onEvent(TeacherSecretariesEvent.SetPhone(it)) }
                }
                Div(attrs = { classes("mt-4") }) {
                    PrimaryButton("Create Secretary", loading = state.saving) { model.onEvent(TeacherSecretariesEvent.Create) }
                }
            }

            if (state.mySecretaries.isEmpty()) {
                EmptyState("🗂️", "No secretaries yet.")
            } else {
                Card {
                    Div(attrs = { classes("divide-y", "divide-default") }) {
                        state.mySecretaries.forEach { secretary ->
                            Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                Avatar(initials = secretary.user.firstName.take(1).uppercase())
                                Div {
                                    P(attrs = { classes("font-medium", "text-heading") }) { Text(secretary.user.fullName) }
                                    P(attrs = { classes("text-sm", "text-body-subtle") }) { Text(secretary.user.email) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
