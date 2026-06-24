package com.wadii.pages.shared

import androidx.compose.runtime.*
import com.wadii.ui.LoadingScreen
import com.wadii.viewmodel.NotificationsEvent
import com.wadii.viewmodel.NotificationsScreenModel
import com.wadii.viewmodel.UiState
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.dom.*

@Composable
fun NotificationsPage() {
    val model = rememberScreenModel { NotificationsScreenModel() }

    Div(attrs = { classes("space-y-6") }) {
        when (val s = model.state) {
            is UiState.Loading -> {
                H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Notifications") }
                LoadingScreen()
            }
            is UiState.Error -> Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) { Text(s.message) }
            is UiState.Success -> {
                val notifications = s.data.notifications
                val unread = notifications.count { !it.isRead }
                Div(attrs = { classes("flex", "items-center", "justify-between") }) {
                    Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Notifications") }
                        if (unread > 0) Span(attrs = { classes("px-2.5", "py-0.5", "bg-red-500", "text-white", "text-xs", "font-bold", "rounded-full") }) { Text(unread.toString()) }
                    }
                    if (unread > 0) Button(attrs = {
                        classes("text-sm", "text-amber-600", "font-medium", "hover:underline")
                        onClick { model.onEvent(NotificationsEvent.MarkAllRead) }
                    }) { Text("Mark all as read") }
                }
                if (notifications.isEmpty()) {
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                        P(attrs = { classes("text-5xl", "mb-3") }) { Text("🔔") }
                        P(attrs = { classes("text-slate-500") }) { Text("No notifications yet.") }
                    }
                } else {
                    Div(attrs = { classes("space-y-2") }) {
                        notifications.forEach { notif ->
                            Div(attrs = {
                                classes("flex", "items-start", "gap-4", "bg-white", "border", "rounded-xl", "px-5", "py-4")
                                if (!notif.isRead) classes("border-amber-200", "bg-amber-50") else classes("border-slate-200")
                            }) {
                                Div(attrs = { classes("w-10", "h-10", "rounded-full", "flex-shrink-0", "flex", "items-center", "justify-center",
                                    if (!notif.isRead) "bg-amber-100" else "bg-slate-100") }) {
                                    Text(when {
                                        notif.title.contains("order", ignoreCase = true) -> "📦"
                                        notif.title.contains("offer", ignoreCase = true) -> "🏷️"
                                        notif.title.contains("message", ignoreCase = true) -> "💬"
                                        else -> "🔔"
                                    })
                                }
                                Div(attrs = { classes("flex-1", "min-w-0") }) {
                                    P(attrs = { classes("font-medium", "text-slate-800") }) { Text(notif.title) }
                                    P(attrs = { classes("text-sm", "text-slate-500", "mt-0.5") }) { Text(notif.body) }
                                    P(attrs = { classes("text-xs", "text-slate-400", "mt-1") }) { Text(notif.createdAt.take(16).replace("T", " ")) }
                                }
                                Div(attrs = { classes("flex", "flex-col", "gap-1", "flex-shrink-0") }) {
                                    if (!notif.isRead) Button(attrs = {
                                        classes("p-1.5", "text-amber-500", "hover:bg-amber-50", "rounded-lg", "text-xs", "whitespace-nowrap")
                                        onClick { model.onEvent(NotificationsEvent.MarkRead(notif.id)) }
                                    }) { Text("✓ Read") }
                                    Button(attrs = {
                                        classes("p-1.5", "text-slate-400", "hover:text-red-500", "hover:bg-red-50", "rounded-lg")
                                        onClick { model.onEvent(NotificationsEvent.Delete(notif.id)) }
                                    }) { Text("🗑️") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
