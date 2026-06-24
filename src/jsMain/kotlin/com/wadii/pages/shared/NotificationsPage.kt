package com.wadii.pages.shared

import androidx.compose.runtime.*
import com.wadii.api.apiDeleteNotification
import com.wadii.api.apiGetNotifications
import com.wadii.api.apiMarkAllRead
import com.wadii.api.apiMarkRead
import com.wadii.model.UserNotification
import com.wadii.state.AppState
import com.wadii.ui.LoadingSkeletons
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.*

@Composable
fun NotificationsPage() {
    var notifications by remember { mutableStateOf<List<UserNotification>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val reload: () -> Unit = { scope.launch { notifications = apiGetNotifications(); loading = false } }
    LaunchedEffect(Unit) { reload() }

    val unreadCount = notifications.count { !it.isRead }

    Div(attrs = { classes("space-y-6") }) {
        Div(attrs = { classes("flex", "items-center", "justify-between") }) {
            Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Notifications") }
                if (unreadCount > 0) {
                    Span(attrs = { classes("px-2.5", "py-0.5", "bg-red-500", "text-white",
                        "text-xs", "font-bold", "rounded-full") }) {
                        Text(unreadCount.toString())
                    }
                }
            }
            if (unreadCount > 0) {
                Button(attrs = {
                    classes("text-sm", "text-amber-600", "font-medium", "hover:underline")
                    onClick {
                        scope.launch {
                            if (apiMarkAllRead()) { reload(); AppState.toast("All marked as read") }
                        }
                    }
                }) { Text("Mark all as read") }
            }
        }

        if (loading) { LoadingSkeletons() }
        else if (notifications.isEmpty()) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                P(attrs = { classes("text-5xl", "mb-3") }) { Text("🔔") }
                P(attrs = { classes("text-slate-500") }) { Text("No notifications yet.") }
            }
        } else {
            Div(attrs = { classes("space-y-2") }) {
                notifications.forEach { notif ->
                    Div(attrs = {
                        classes("flex", "items-start", "gap-4", "bg-white",
                            "border", "rounded-xl", "px-5", "py-4",
                            if (!notif.isRead) "border-amber-200 bg-amber-50" else "border-slate-200")
                    }) {
                        Div(attrs = { classes("w-10", "h-10", "rounded-full", "flex-shrink-0",
                            "flex", "items-center", "justify-center",
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
                            P(attrs = { classes("text-xs", "text-slate-400", "mt-1") }) {
                                Text(notif.createdAt.take(16).replace("T", " "))
                            }
                        }
                        Div(attrs = { classes("flex", "flex-col", "gap-1", "flex-shrink-0") }) {
                            if (!notif.isRead) {
                                Button(attrs = {
                                    classes("p-1.5", "text-amber-500", "hover:bg-amber-50",
                                        "rounded-lg", "text-xs", "whitespace-nowrap")
                                    onClick {
                                        scope.launch {
                                            apiMarkRead(notif.id)
                                            notifications = notifications.map { n ->
                                                if (n.id == notif.id) n.copy(isRead = true) else n
                                            }
                                        }
                                    }
                                }) { Text("✓ Read") }
                            }
                            Button(attrs = {
                                classes("p-1.5", "text-slate-400", "hover:text-red-500",
                                    "hover:bg-red-50", "rounded-lg")
                                onClick {
                                    scope.launch {
                                        if (apiDeleteNotification(notif.id)) {
                                            notifications = notifications.filter { it.id != notif.id }
                                        }
                                    }
                                }
                            }) { Text("🗑️") }
                        }
                    }
                }
            }
        }
    }
}
