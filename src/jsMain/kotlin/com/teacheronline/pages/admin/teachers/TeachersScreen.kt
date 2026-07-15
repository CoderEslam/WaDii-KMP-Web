package com.teacheronline.pages.admin.teachers

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Avatar
import com.teacheronline.ui.Card
import com.teacheronline.ui.Checkbox
import com.teacheronline.ui.EmptyState
import com.teacheronline.ui.GhostButton
import com.teacheronline.ui.InputField
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.PrimaryButton
import com.teacheronline.ui.SecondaryButton
import org.jetbrains.compose.web.dom.*

class TeachersScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<TeachersViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Teachers") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    Card(classes = "p-5") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) {
                            Text(if (state.editingId != 0L) "Edit Teacher" else "Add Teacher")
                        }
                        Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                            InputField("First name", state.firstName, required = true) { model.onEvent(TeachersEvent.SetFirstName(it)) }
                            InputField("Last name", state.lastName, required = true) { model.onEvent(TeachersEvent.SetLastName(it)) }
                            InputField("Email", state.email, type = "email") { model.onEvent(TeachersEvent.SetEmail(it)) }
                            InputField("Password", state.password, type = "password") { model.onEvent(TeachersEvent.SetPassword(it)) }
                            InputField("Phone", state.phone) { model.onEvent(TeachersEvent.SetPhone(it)) }
                        }
                        if (state.subjects.isNotEmpty()) {
                            Div(attrs = { classes("mt-4") }) {
                                P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) { Text("Subjects") }
                                Div(attrs = { classes("flex", "flex-wrap", "gap-4") }) {
                                    state.subjects.forEach { subject ->
                                        Checkbox(
                                            id = "subject-${subject.id}",
                                            label = subject.name,
                                            checked = subject.id in state.selectedSubjectIds,
                                            onCheckedChange = { model.onEvent(TeachersEvent.ToggleSubject(subject.id)) }
                                        )
                                    }
                                }
                            }
                        }
                        Div(attrs = { classes("flex", "gap-3", "mt-4") }) {
                            PrimaryButton(if (state.editingId != 0L) "Save" else "Add", loading = state.saving) {
                                model.onEvent(TeachersEvent.Save)
                            }
                            if (state.editingId != 0L) {
                                SecondaryButton("Cancel") { model.onEvent(TeachersEvent.CancelEdit) }
                            }
                        }
                    }

                    if (state.teachers.isEmpty()) {
                        EmptyState("🧑‍🏫", "No teachers yet.")
                    } else {
                        Card {
                            Div(attrs = { classes("divide-y", "divide-default") }) {
                                state.teachers.forEach { teacher ->
                                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                        Avatar(initials = teacher.user.firstName.take(1).uppercase())
                                        Div(attrs = { classes("flex-1") }) {
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(teacher.user.fullName) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) {
                                                Text("${teacher.user.email} · ${teacher.phone} · ${teacher.subjects.joinToString { it.name }}")
                                            }
                                        }
                                        GhostButton("✏️") { model.onEvent(TeachersEvent.StartEdit(teacher)) }
                                        GhostButton("🗑️") { model.onEvent(TeachersEvent.Delete(teacher.id)) }
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
