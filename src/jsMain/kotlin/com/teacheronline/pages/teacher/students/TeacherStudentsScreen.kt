package com.teacheronline.pages.teacher.students

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Avatar
import com.teacheronline.ui.Card
import com.teacheronline.ui.EmptyState
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.SelectField
import org.jetbrains.compose.web.dom.*

class TeacherStudentsScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<TeacherStudentsViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("My Students") }

            if (state.error != null) Alert(variant = AlertVariant.Danger, body = state.error!!)

            if (state.mySubjects.isNotEmpty()) {
                Card(classes = "p-5") {
                    SelectField(
                        "Subject", state.mySubjects,
                        state.mySubjects.find { it.id == state.selectedSubjectId } ?: state.mySubjects.first(),
                        { it.name }
                    ) { model.onEvent(TeacherStudentsEvent.SelectSubject(it.id)) }
                }
            }

            if (state.isLoading) {
                LoadingScreen()
            } else if (state.students.isEmpty()) {
                EmptyState("🎓", "No students enrolled in this subject.")
            } else {
                Card {
                    Div(attrs = { classes("divide-y", "divide-default") }) {
                        state.students.forEach { student ->
                            Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                Avatar(initials = student.user.firstName.take(1).uppercase())
                                Div {
                                    P(attrs = { classes("font-medium", "text-heading") }) { Text(student.user.fullName) }
                                    P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("${student.user.email} · ${student.level.name}") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
