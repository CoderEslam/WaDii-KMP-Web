package com.teacheronline.pages.teacher.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.teacheronline.pages.teacher.attendance.TeacherAttendanceScreen
import com.teacheronline.pages.teacher.schedule.TeacherScheduleScreen
import com.teacheronline.pages.teacher.students.TeacherStudentsScreen
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Card
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.StatCard
import org.jetbrains.compose.web.dom.*

class TeacherDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<TeacherDashboardViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-8") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Teacher Dashboard") }
            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                state.me == null -> Alert(variant = AlertVariant.Warning, body = "No teacher profile found for your account yet.")
                else -> {
                    val me = state.me!!
                    Div(attrs = { classes("grid", "grid-cols-2", "md:grid-cols-3", "gap-4") }) {
                        StatCard("My Subjects", me.subjects.size.toString(), "📚") {}
                        StatCard("Attendance", "", "🗓️") { navigator.push(TeacherAttendanceScreen()) }
                        StatCard("Schedule", "", "🕐") { navigator.push(TeacherScheduleScreen()) }
                    }
                    Card(classes = "p-5") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("My Subjects") }
                        if (me.subjects.isEmpty()) {
                            P(attrs = { classes("text-body-subtle", "text-sm") }) { Text("No subjects assigned yet.") }
                        } else {
                            Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                                me.subjects.forEach { subject ->
                                    Span(attrs = { classes("px-3", "py-1", "bg-brand-softer", "text-fg-brand-strong", "rounded-neu-default", "text-sm") }) {
                                        Text(subject.name)
                                    }
                                }
                            }
                        }
                    }
                    Div(attrs = { classes("flex", "gap-3") }) {
                        Span(attrs = {
                            classes("text-fg-brand", "font-medium", "cursor-pointer", "hover:underline")
                            onClick { navigator.push(TeacherStudentsScreen()) }
                        }) { Text("View my students →") }
                    }
                }
            }
        }
    }
}
