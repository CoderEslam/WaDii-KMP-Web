package com.teacheronline.pages.admin.students

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
import com.teacheronline.ui.RadioGroup
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
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Enroll Student") }
                        P(attrs = { classes("text-xs", "text-body-subtle", "mb-3") }) {
                            Text("Every student belongs to a parent. Create a new parent for a first enrollment, or pick an existing parent to add another kid to their account.")
                        }

                        RadioGroup(
                            name = "enroll-mode",
                            options = listOf(EnrollMode.NEW_PARENT, EnrollMode.EXISTING_PARENT),
                            selected = state.enrollMode,
                            itemLabel = { if (it == EnrollMode.NEW_PARENT) "New parent" else "Existing parent" }
                        ) { model.onEvent(AdminStudentsEvent.SetEnrollMode(it)) }

                        if (state.enrollMode == EnrollMode.NEW_PARENT) {
                            Div(attrs = { classes("mt-4") }) {
                                P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) { Text("Parent") }
                                Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                                    InputField("Parent first name", state.parentFirstName, required = true) { model.onEvent(AdminStudentsEvent.SetParentFirstName(it)) }
                                    InputField("Parent last name", state.parentLastName, required = true) { model.onEvent(AdminStudentsEvent.SetParentLastName(it)) }
                                    InputField("Parent email", state.parentEmail, type = "email", required = true) { model.onEvent(AdminStudentsEvent.SetParentEmail(it)) }
                                    InputField("Parent password", state.parentPassword, type = "password", required = true) { model.onEvent(AdminStudentsEvent.SetParentPassword(it)) }
                                    InputField("Parent phone", state.parentPhone) { model.onEvent(AdminStudentsEvent.SetParentPhone(it)) }
                                    InputField("Parent contact", state.parentContact) { model.onEvent(AdminStudentsEvent.SetParentContact(it)) }
                                }
                            }
                        } else {
                            Div(attrs = { classes("mt-4") }) {
                                if (state.parents.isNotEmpty()) {
                                    SelectField(
                                        "Parent",
                                        options = state.parents,
                                        selected = state.parents.find { it.id == state.selectedParentId } ?: state.parents.first(),
                                        optionLabel = { it.user.fullName.ifBlank { it.user.email } }
                                    ) { model.onEvent(AdminStudentsEvent.SetSelectedParent(it.id)) }
                                } else {
                                    P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("No parents yet — switch to \"New parent\" to create one.") }
                                }
                            }
                        }

                        Div(attrs = { classes("mt-4") }) {
                            P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) { Text("Student") }
                            Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                                InputField("First name", state.firstName, required = true) { model.onEvent(AdminStudentsEvent.SetFirstName(it)) }
                                InputField("Last name", state.lastName, required = true) { model.onEvent(AdminStudentsEvent.SetLastName(it)) }
                                InputField("Email", state.email, type = "email", required = true) { model.onEvent(AdminStudentsEvent.SetEmail(it)) }
                                InputField("Password", state.password, type = "password", required = true) { model.onEvent(AdminStudentsEvent.SetPassword(it)) }
                                InputField("Phone", state.phone) { model.onEvent(AdminStudentsEvent.SetPhone(it)) }
                                if (state.levels.isNotEmpty()) {
                                    SelectField(
                                        "Level",
                                        options = state.levels,
                                        selected = state.levels.find { it.id == state.levelId } ?: state.levels.first(),
                                        optionLabel = { it.name }
                                    ) { model.onEvent(AdminStudentsEvent.SetLevel(it.id)) }
                                }
                            }
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
                            PrimaryButton("Enroll Student", loading = state.saving) { model.onEvent(AdminStudentsEvent.Enroll) }
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
