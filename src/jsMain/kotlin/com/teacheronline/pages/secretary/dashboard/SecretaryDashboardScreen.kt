package com.teacheronline.pages.secretary.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.domain.model.AttendanceStatus
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Avatar
import com.teacheronline.ui.Badge
import com.teacheronline.ui.BadgeVariant
import com.teacheronline.ui.Card
import com.teacheronline.ui.Checkbox
import com.teacheronline.ui.EmptyState
import com.teacheronline.ui.GhostButton
import com.teacheronline.ui.InputField
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.PrimaryButton
import com.teacheronline.ui.SelectField
import com.teacheronline.ui.Tabs
import org.jetbrains.compose.web.dom.*

class SecretaryDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<SecretaryDashboardViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Secretary Console") }
            if (state.error != null) Alert(variant = AlertVariant.Danger, body = state.error!!)
            if (state.isLoading) LoadingScreen()

            Tabs(
                tabs = listOf("Students", "Attendance", "Payments"),
                selected = state.tab,
                onSelect = { model.onEvent(SecretaryDashboardEvent.SetTab(it)) }
            )

            when (state.tab) {
                0 -> StudentsTab(state, model)
                1 -> AttendanceTab(state, model)
                else -> PaymentsTab(state, model)
            }
        }
    }
}

@Composable
private fun StudentsTab(state: SecretaryDashboardState, model: SecretaryDashboardViewModel) {
    Card(classes = "p-5") {
        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Add Student") }
        Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
            InputField("First name", state.firstName, required = true) { model.onEvent(SecretaryDashboardEvent.SetFirstName(it)) }
            InputField("Last name", state.lastName, required = true) { model.onEvent(SecretaryDashboardEvent.SetLastName(it)) }
            InputField("Email", state.email, type = "email", required = true) { model.onEvent(SecretaryDashboardEvent.SetEmail(it)) }
            InputField("Password", state.password, type = "password", required = true) { model.onEvent(SecretaryDashboardEvent.SetPassword(it)) }
            InputField("Phone", state.phone) { model.onEvent(SecretaryDashboardEvent.SetPhone(it)) }
            InputField("Parent contact", state.parentContact) { model.onEvent(SecretaryDashboardEvent.SetParentContact(it)) }
            InputField("Parent user ID", state.parentId, type = "number") { model.onEvent(SecretaryDashboardEvent.SetParentId(it)) }
            if (state.levels.isNotEmpty()) {
                SelectField(
                    "Level", state.levels,
                    state.levels.find { it.id == state.levelId } ?: state.levels.first(),
                    { it.name }
                ) { model.onEvent(SecretaryDashboardEvent.SetLevel(it.id)) }
            }
        }
        if (state.subjects.isNotEmpty()) {
            Div(attrs = { classes("mt-4") }) {
                P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) { Text("Subjects") }
                Div(attrs = { classes("flex", "flex-wrap", "gap-4") }) {
                    state.subjects.forEach { subject ->
                        Checkbox(
                            id = "sec-student-subject-${subject.id}",
                            label = subject.name,
                            checked = subject.id in state.selectedSubjectIds,
                            onCheckedChange = { model.onEvent(SecretaryDashboardEvent.ToggleSubject(subject.id)) }
                        )
                    }
                }
            }
        }
        Div(attrs = { classes("mt-4") }) {
            PrimaryButton("Create Student", loading = state.saving) { model.onEvent(SecretaryDashboardEvent.CreateStudent) }
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
                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("${student.user.email} · ${student.level.name}") }
                        }
                        GhostButton("🗑️") { model.onEvent(SecretaryDashboardEvent.DeleteStudent(student.id)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceTab(state: SecretaryDashboardState, model: SecretaryDashboardViewModel) {
    Card(classes = "p-5") {
        Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
            if (state.students.isNotEmpty()) {
                SelectField(
                    "Student", state.students,
                    state.students.find { it.id == state.attendanceStudentId } ?: state.students.first(),
                    { it.user.fullName }
                ) { model.onEvent(SecretaryDashboardEvent.SetAttendanceStudent(it.id)) }
            }
            if (state.subjects.isNotEmpty()) {
                SelectField(
                    "Subject", state.subjects,
                    state.subjects.find { it.id == state.attendanceSubjectId } ?: state.subjects.first(),
                    { it.name }
                ) { model.onEvent(SecretaryDashboardEvent.SetAttendanceSubject(it.id)) }
            }
            if (state.teachers.isNotEmpty()) {
                SelectField(
                    "Teacher", state.teachers,
                    state.teachers.find { it.id == state.attendanceTeacherId } ?: state.teachers.first(),
                    { it.user.fullName }
                ) { model.onEvent(SecretaryDashboardEvent.SetAttendanceTeacher(it.id)) }
            }
            SelectField("Status", AttendanceStatus.entries.toList(), state.attendanceStatus, { it.name }) {
                model.onEvent(SecretaryDashboardEvent.SetAttendanceStatus(it))
            }
        }
        Div(attrs = { classes("mt-4") }) {
            PrimaryButton("Submit", loading = state.saving) { model.onEvent(SecretaryDashboardEvent.SubmitAttendance) }
        }
    }

    if (state.attendanceHistory.isEmpty()) {
        EmptyState("🗓️", "No attendance records yet.")
    } else {
        Card {
            Div(attrs = { classes("divide-y", "divide-default") }) {
                state.attendanceHistory.forEach { record ->
                    Div(attrs = { classes("flex", "items-center", "justify-between", "px-5", "py-4") }) {
                        Div {
                            P(attrs = { classes("font-medium", "text-heading") }) { Text(record.student.user.fullName) }
                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("${record.subject.name} · ${record.date}") }
                        }
                        Badge(
                            record.status.name,
                            variant = when (record.status) {
                                AttendanceStatus.PRESENT -> BadgeVariant.Success
                                AttendanceStatus.LATE -> BadgeVariant.Warning
                                AttendanceStatus.ABSENT -> BadgeVariant.Danger
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentsTab(state: SecretaryDashboardState, model: SecretaryDashboardViewModel) {
    Card(classes = "p-5") {
        Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
            if (state.students.isNotEmpty()) {
                SelectField(
                    "Student", state.students,
                    state.students.find { it.id == state.paymentStudentId } ?: state.students.first(),
                    { it.user.fullName }
                ) { model.onEvent(SecretaryDashboardEvent.SetPaymentStudent(it.id)) }
            }
            if (state.subjects.isNotEmpty()) {
                SelectField(
                    "Subject", state.subjects,
                    state.subjects.find { it.id == state.paymentSubjectId } ?: state.subjects.first(),
                    { it.name }
                ) { model.onEvent(SecretaryDashboardEvent.SetPaymentSubject(it.id)) }
            }
            if (state.teachers.isNotEmpty()) {
                SelectField(
                    "Teacher", state.teachers,
                    state.teachers.find { it.id == state.paymentTeacherId } ?: state.teachers.first(),
                    { it.user.fullName }
                ) { model.onEvent(SecretaryDashboardEvent.SetPaymentTeacher(it.id)) }
            }
            InputField("Price", state.paymentPrice, type = "number") { model.onEvent(SecretaryDashboardEvent.SetPaymentPrice(it)) }
            InputField("Notes", state.paymentNotes) { model.onEvent(SecretaryDashboardEvent.SetPaymentNotes(it)) }
        }
        Div(attrs = { classes("mt-4") }) {
            PrimaryButton("Record Payment", loading = state.saving) { model.onEvent(SecretaryDashboardEvent.SubmitPayment) }
        }
    }
}
