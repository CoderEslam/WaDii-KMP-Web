package com.teacheronline.pages.teacher.payments

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Card
import com.teacheronline.ui.InputField
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.PrimaryButton
import com.teacheronline.ui.SelectField
import org.jetbrains.compose.web.dom.*

class TeacherPaymentsScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<TeacherPaymentsViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Record Student Payment") }

            if (state.error != null) Alert(variant = AlertVariant.Danger, body = state.error!!)
            if (state.isLoading) LoadingScreen()

            Card(classes = "p-5") {
                Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                    if (state.mySubjects.isNotEmpty()) {
                        SelectField(
                            "Subject", state.mySubjects,
                            state.mySubjects.find { it.id == state.selectedSubjectId } ?: state.mySubjects.first(),
                            { it.name }
                        ) { model.onEvent(TeacherPaymentsEvent.SelectSubject(it.id)) }
                    }
                    if (state.studentsInSubject.isNotEmpty()) {
                        SelectField(
                            "Student", state.studentsInSubject,
                            state.studentsInSubject.find { it.id == state.selectedStudentId } ?: state.studentsInSubject.first(),
                            { it.user.fullName }
                        ) { model.onEvent(TeacherPaymentsEvent.SelectStudent(it.id)) }
                    }
                    InputField("Price", state.price, type = "number") { model.onEvent(TeacherPaymentsEvent.SetPrice(it)) }
                    InputField("Notes", state.notes) { model.onEvent(TeacherPaymentsEvent.SetNotes(it)) }
                }
                Div(attrs = { classes("mt-4") }) {
                    PrimaryButton("Record Payment", loading = state.saving) { model.onEvent(TeacherPaymentsEvent.Submit) }
                }
            }
        }
    }
}
