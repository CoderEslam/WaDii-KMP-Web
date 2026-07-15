package com.teacheronline.pages.teacher.attendance

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.domain.model.AttendanceStatus
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Badge
import com.teacheronline.ui.BadgeVariant
import com.teacheronline.ui.Card
import com.teacheronline.ui.EmptyState
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.PrimaryButton
import com.teacheronline.ui.SelectField
import org.jetbrains.compose.web.dom.*

class TeacherAttendanceScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<TeacherAttendanceViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Attendance") }

            if (state.error != null) Alert(variant = AlertVariant.Danger, body = state.error!!)

            if (state.isLoading) LoadingScreen()

            Card(classes = "p-5") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Mark Attendance") }
                Div(attrs = { classes("grid", "gap-3", "md:grid-cols-3") }) {
                    if (state.mySubjects.isNotEmpty()) {
                        SelectField(
                            "Subject", state.mySubjects,
                            state.mySubjects.find { it.id == state.selectedSubjectId } ?: state.mySubjects.first(),
                            { it.name }
                        ) { model.onEvent(TeacherAttendanceEvent.SelectSubject(it.id)) }
                    }
                    if (state.studentsInSubject.isNotEmpty()) {
                        SelectField(
                            "Student", state.studentsInSubject,
                            state.studentsInSubject.find { it.id == state.selectedStudentId } ?: state.studentsInSubject.first(),
                            { it.user.fullName }
                        ) { model.onEvent(TeacherAttendanceEvent.SelectStudent(it.id)) }
                    }
                    SelectField(
                        "Status", AttendanceStatus.entries.toList(), state.status, { it.name }
                    ) { model.onEvent(TeacherAttendanceEvent.SetStatus(it)) }
                }
                Div(attrs = { classes("mt-4") }) {
                    PrimaryButton("Submit", loading = state.saving) { model.onEvent(TeacherAttendanceEvent.Submit) }
                }
            }

            if (state.history.isEmpty()) {
                EmptyState("🗓️", "No attendance records yet.")
            } else {
                Card {
                    Div(attrs = { classes("divide-y", "divide-default") }) {
                        state.history.forEach { record ->
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
    }
}
