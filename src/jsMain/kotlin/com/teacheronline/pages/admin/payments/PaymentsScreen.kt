package com.teacheronline.pages.admin.payments

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Card
import com.teacheronline.ui.EmptyState
import com.teacheronline.ui.InputField
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.PrimaryButton
import com.teacheronline.ui.SecondaryButton
import com.teacheronline.ui.SelectField
import com.teacheronline.ui.Tabs
import org.jetbrains.compose.web.dom.*

class PaymentsScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<PaymentsViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Payments") }
            if (state.error != null) Alert(variant = AlertVariant.Danger, body = state.error!!)

            Tabs(
                tabs = listOf("Extra Income", "Outgoing", "Student Payments"),
                selected = state.tab,
                onSelect = { model.onEvent(PaymentsEvent.SetTab(it)) }
            )

            when (state.tab) {
                0 -> Card(classes = "p-5") {
                    Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                        InputField("Price", state.extraPrice, type = "number") { model.onEvent(PaymentsEvent.SetExtraPrice(it)) }
                        InputField("Notes", state.extraNotes) { model.onEvent(PaymentsEvent.SetExtraNotes(it)) }
                    }
                    Div(attrs = { classes("mt-4") }) {
                        PrimaryButton("Record", loading = state.saving) { model.onEvent(PaymentsEvent.CreateExtra) }
                    }
                }

                1 -> Card(classes = "p-5") {
                    Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                        InputField("Price", state.outPrice, type = "number") { model.onEvent(PaymentsEvent.SetOutPrice(it)) }
                        InputField("Notes", state.outNotes) { model.onEvent(PaymentsEvent.SetOutNotes(it)) }
                    }
                    Div(attrs = { classes("mt-4") }) {
                        PrimaryButton("Record", loading = state.saving) { model.onEvent(PaymentsEvent.CreateOut) }
                    }
                }

                else -> {
                    Card(classes = "p-5") {
                        Div(attrs = { classes("grid", "gap-3", "md:grid-cols-2") }) {
                            if (state.students.isNotEmpty()) {
                                SelectField(
                                    "Student", state.students,
                                    state.students.find { it.id == state.studentId } ?: state.students.first(),
                                    { it.user.fullName }
                                ) { model.onEvent(PaymentsEvent.SetStudent(it.id)) }
                            }
                            if (state.teachers.isNotEmpty()) {
                                SelectField(
                                    "Teacher", state.teachers,
                                    state.teachers.find { it.id == state.teacherId } ?: state.teachers.first(),
                                    { it.user.fullName }
                                ) { model.onEvent(PaymentsEvent.SetTeacher(it.id)) }
                            }
                            val selectedStudentSubjects = state.students.find { it.id == state.studentId }?.subjects ?: emptyList()
                            if (selectedStudentSubjects.isNotEmpty()) {
                                SelectField(
                                    "Subject", selectedStudentSubjects,
                                    selectedStudentSubjects.find { it.id == state.subjectId } ?: selectedStudentSubjects.first(),
                                    { it.name }
                                ) { model.onEvent(PaymentsEvent.SetSubject(it.id)) }
                            }
                            InputField("Price", state.studentPrice, type = "number") { model.onEvent(PaymentsEvent.SetStudentPrice(it)) }
                            InputField("Notes", state.studentNotes) { model.onEvent(PaymentsEvent.SetStudentNotes(it)) }
                        }
                        Div(attrs = { classes("flex", "gap-3", "mt-4") }) {
                            PrimaryButton("Record Payment", loading = state.saving) { model.onEvent(PaymentsEvent.CreateStudentPayment) }
                            SecondaryButton("Lookup History") { model.onEvent(PaymentsEvent.LookupStudentPayments) }
                        }
                    }

                    if (state.isLoading) LoadingScreen()

                    if (state.lookedUpPayments.isEmpty()) {
                        EmptyState("💳", "No subscription history looked up yet.")
                    } else {
                        Card {
                            Div(attrs = { classes("divide-y", "divide-default") }) {
                                state.lookedUpPayments.forEach { p ->
                                    Div(attrs = { classes("flex", "items-center", "justify-between", "px-5", "py-4") }) {
                                        Div {
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(p.subject.name) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("${p.startDate} → ${p.endDate} · $${p.price}") }
                                        }
                                        P(attrs = {
                                            classes("text-sm", "font-medium", if (p.accessValid) "text-fg-success" else "text-fg-danger")
                                        }) { Text(if (p.accessValid) "Active" else "Expired") }
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
