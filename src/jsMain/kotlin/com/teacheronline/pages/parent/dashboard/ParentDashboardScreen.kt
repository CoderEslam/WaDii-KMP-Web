package com.teacheronline.pages.parent.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.ui.Accordion
import com.teacheronline.ui.AccordionItem
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Avatar
import com.teacheronline.ui.EmptyState
import com.teacheronline.ui.LoadingScreen
import org.jetbrains.compose.web.dom.*

class ParentDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<ParentDashboardViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("My Children") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                state.children.isEmpty() -> EmptyState("👨‍👩‍👧", "No children linked to your account yet.")
                else -> {
                    Accordion(
                        items = state.children.map { child ->
                            AccordionItem(title = "${child.user.fullName} · ${child.level.name}") {
                                Div(attrs = { classes("space-y-4") }) {
                                    Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                                        Avatar(initials = child.user.firstName.take(1).uppercase())
                                        Div {
                                            P(attrs = { classes("font-medium", "text-heading") }) { Text(child.user.fullName) }
                                            P(attrs = { classes("text-xs", "text-body-subtle") }) { Text(child.user.email) }
                                        }
                                    }
                                    Div {
                                        P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-1") }) { Text("Subjects") }
                                        if (child.subjects.isEmpty()) {
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("None yet.") }
                                        } else {
                                            Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                                                child.subjects.forEach { subject ->
                                                    Span(attrs = { classes("px-3", "py-1", "bg-brand-softer", "text-fg-brand-strong", "rounded-neu-default", "text-sm") }) {
                                                        Text(subject.name)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Div {
                                        P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-1") }) { Text("Subscription status") }
                                        val statuses = state.statusByStudentId[child.id] ?: emptyList()
                                        if (statuses.isEmpty()) {
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("No payment history yet.") }
                                        } else {
                                            statuses.forEach { status ->
                                                Div(attrs = { classes("flex", "items-center", "justify-between", "py-1") }) {
                                                    P(attrs = { classes("text-sm", "text-body") }) { Text(status.subjectName) }
                                                    P(attrs = {
                                                        classes("text-xs", "font-medium", if (status.accessValid) "text-fg-success" else "text-fg-danger")
                                                    }) { Text(if (status.accessValid) "Active" else "Expired") }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
