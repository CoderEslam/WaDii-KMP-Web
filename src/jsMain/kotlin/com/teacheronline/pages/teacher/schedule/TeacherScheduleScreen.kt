package com.teacheronline.pages.teacher.schedule

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

class TeacherScheduleScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<TeacherScheduleViewModel>()
        val state by model.state.collectAsState()
        val mySubjects = state.me?.subjects ?: emptyList()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("My Schedule") }
            Alert(variant = AlertVariant.Brand, body = "The API only supports creating schedule slots — slots created this session are listed below.")

            if (state.error != null) Alert(variant = AlertVariant.Danger, body = state.error!!)
            if (state.isLoading) LoadingScreen()

            Card(classes = "p-5") {
                Div(attrs = { classes("grid", "gap-3", "md:grid-cols-3") }) {
                    SelectField("Day", DAYS, state.dayOfWeek, { it }) { model.onEvent(TeacherScheduleEvent.SetDay(it)) }
                    InputField("Start (HH:mm:ss)", state.timeSlotStart) { model.onEvent(TeacherScheduleEvent.SetStart(it)) }
                    InputField("End (HH:mm:ss)", state.timeSlotEnd) { model.onEvent(TeacherScheduleEvent.SetEnd(it)) }
                    if (mySubjects.isNotEmpty()) {
                        SelectField(
                            "Subject", mySubjects,
                            mySubjects.find { it.id == state.subjectId } ?: mySubjects.first(),
                            { it.name }
                        ) { model.onEvent(TeacherScheduleEvent.SetSubject(it.id)) }
                    }
                }
                Div(attrs = { classes("mt-4") }) {
                    PrimaryButton("Create Slot", loading = state.saving) { model.onEvent(TeacherScheduleEvent.Create) }
                }
            }

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
