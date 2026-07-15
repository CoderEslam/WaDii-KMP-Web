package com.teacheronline.pages.student.dashboard

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
import org.jetbrains.compose.web.dom.*

class StudentDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<StudentDashboardViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("My Dashboard") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                state.me == null -> Alert(variant = AlertVariant.Warning, body = "No student profile found for your account yet.")
                else -> {
                    val me = state.me!!
                    Card(classes = "p-5") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("My Subjects") }
                        if (me.subjects.isEmpty()) {
                            P(attrs = { classes("text-body-subtle", "text-sm") }) { Text("No subjects enrolled yet.") }
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

                    Card(classes = "p-5") {
                        H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("Subscription Status") }
                        if (state.subjectsStatus.isEmpty()) {
                            P(attrs = { classes("text-body-subtle", "text-sm") }) { Text("No payment history yet.") }
                        } else {
                            Div(attrs = { classes("divide-y", "divide-default") }) {
                                state.subjectsStatus.forEach { status ->
                                    Div(attrs = { classes("flex", "items-center", "justify-between", "py-3") }) {
                                        Div {
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(status.subjectName) }
                                            P(attrs = { classes("text-xs", "text-body-subtle") }) { Text("${status.startDate} → ${status.endDate}") }
                                        }
                                        P(attrs = {
                                            classes("text-sm", "font-medium", if (status.accessValid) "text-fg-success" else "text-fg-danger")
                                        }) { Text(if (status.accessValid) "Active" else "Expired") }
                                    }
                                }
                            }
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
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(record.subject.name) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text(record.date) }
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
    }
}
