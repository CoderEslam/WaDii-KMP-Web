package com.teacheronline.pages.admin.schedule

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
import com.teacheronline.ui.SelectField
import org.jetbrains.compose.web.dom.*

private val DAYS = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

class AdminScheduleScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<AdminScheduleViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Schedule") }
            Alert(variant = AlertVariant.Brand, body = "The API only supports creating schedule slots — there's no listing endpoint yet, so only slots created this session are shown below.")

            if (state.error != null) Alert(variant = AlertVariant.Danger, body = state.error!!)

            Card(classes = "p-5") {
                H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Add Schedule Slot") }
                Div(attrs = { classes("grid", "gap-3", "md:grid-cols-3") }) {
                    SelectField("Day", DAYS, state.dayOfWeek, { it }) { model.onEvent(AdminScheduleEvent.SetDay(it)) }
                    InputField("Start (HH:mm:ss)", state.timeSlotStart) { model.onEvent(AdminScheduleEvent.SetStart(it)) }
                    InputField("End (HH:mm:ss)", state.timeSlotEnd) { model.onEvent(AdminScheduleEvent.SetEnd(it)) }
                    if (state.teachers.isNotEmpty()) {
                        SelectField(
                            "Teacher", state.teachers,
                            state.teachers.find { it.id == state.teacherId } ?: state.teachers.first(),
                            { it.user.fullName }
                        ) { model.onEvent(AdminScheduleEvent.SetTeacher(it.id)) }
                    }
                    if (state.subjects.isNotEmpty()) {
                        SelectField(
                            "Subject", state.subjects,
                            state.subjects.find { it.id == state.subjectId } ?: state.subjects.first(),
                            { it.name }
                        ) { model.onEvent(AdminScheduleEvent.SetSubject(it.id)) }
                    }
                }
                Div(attrs = { classes("mt-4") }) {
                    PrimaryButton("Create Slot", loading = state.saving) { model.onEvent(AdminScheduleEvent.Create) }
                }
            }

            if (state.isLoading) LoadingScreen()

            if (state.createdThisSession.isEmpty()) {
                EmptyState("🗓️", "No schedule slots created this session yet.")
            } else {
                Card {
                    Div(attrs = { classes("divide-y", "divide-default") }) {
                        state.createdThisSession.forEach { schedule ->
                            Div(attrs = { classes("px-5", "py-4") }) {
                                P(attrs = { classes("font-medium", "text-heading") }) { Text(schedule.dayOfWeek) }
                                P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("${schedule.timeSlotStart} – ${schedule.timeSlotEnd}") }
                            }
                        }
                    }
                }
            }
        }
    }
}
