package com.teacheronline.pages.admin.students

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.domain.model.Level
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
import com.teacheronline.ui.SelectField
import org.jetbrains.compose.web.dom.*

class AdminStudentsScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<AdminStudentsViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Students") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    Card(classes = "p-5") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Add Student") }
                        Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                            InputField("First name", state.firstName, required = true) { model.onEvent(AdminStudentsEvent.SetFirstName(it)) }
                            InputField("Last name", state.lastName, required = true) { model.onEvent(AdminStudentsEvent.SetLastName(it)) }
                            InputField("Email", state.email, type = "email", required = true) { model.onEvent(AdminStudentsEvent.SetEmail(it)) }
                            InputField("Password", state.password, type = "password", required = true) { model.onEvent(AdminStudentsEvent.SetPassword(it)) }
                            InputField("Phone", state.phone) { model.onEvent(AdminStudentsEvent.SetPhone(it)) }
                            InputField("Parent contact", state.parentContact) { model.onEvent(AdminStudentsEvent.SetParentContact(it)) }
                            InputField(
                                "Parent user ID",
                                state.parentId,
                                type = "number",
                                placeholder = "existing user id"
                            ) { model.onEvent(AdminStudentsEvent.SetParentId(it)) }
                            if (state.levels.isNotEmpty()) {
                                SelectField(
                                    "Level",
                                    options = state.levels,
                                    selected = state.levels.find { it.id == state.levelId } ?: state.levels.first(),
                                    optionLabel = { it.name }
                                ) { model.onEvent(AdminStudentsEvent.SetLevel(it.id)) }
                            }
                        }
                        P(attrs = { classes("text-xs", "text-body-subtle", "mt-3") }) {
                            Text("Parent user ID must already exist — the API has no lookup endpoint yet, so enter it directly.")
                        }
                        if (state.subjects.isNotEmpty()) {
                            Div(attrs = { classes("mt-4") }) {
                                P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) { Text("Subjects") }
                                Div(attrs = { classes("flex", "flex-wrap", "gap-4") }) {
                                    state.subjects.forEach { subject ->
                                        Checkbox(
                                            id = "student-subject-${subject.id}",
                                            label = subject.name,
                                            checked = subject.id in state.selectedSubjectIds,
                                            onCheckedChange = { model.onEvent(AdminStudentsEvent.ToggleSubject(subject.id)) }
                                        )
                                    }
                                }
                            }
                        }
                        Div(attrs = { classes("mt-4") }) {
                            PrimaryButton("Create Student", loading = state.saving) { model.onEvent(AdminStudentsEvent.Create) }
                        }
                    }

                    if (state.students.isEmpty()) {
                        EmptyState("🎓", "No students yet.")
                    } else {
                        Card {
                            Div(attrs = { classes("divide-y", "divide-default") }) {
                                state.students.forEach { student ->
                                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                        Avatar(initials = student.user.firstName.take(1).uppercase())
                                        Div(attrs = { classes("flex-1") }) {
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(student.user.fullName) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) {
                                                Text("${student.user.email} · ${student.level.name} · ${student.subjects.joinToString { it.name }}")
                                            }
                                        }
                                        GhostButton("🗑️") { model.onEvent(AdminStudentsEvent.Delete(student.id)) }
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
