package com.wadii.pages.shared.notifications

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.domain.model.notification.UserNotification
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.pages.provider.dashboard.ProviderDashboardScreen
import com.wadii.pages.provider.respond.RespondToOrderScreen
import com.wadii.screens.orders.detail.OrderDetailScreen
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.LoadingSkeletons
import org.jetbrains.compose.web.dom.*

/** Server notification types: NEW_ORDER, NEW_RESPONSE, RESPONSE_ACCEPTED, RESPONSE_DECLINED, RESPONSE_REJECTED, ORDER_CANCELED, ORDER_STARTED, ORDER_COMPLETED, PROVIDER_ACCEPTED, PROVIDER_REJECTED. */
private fun iconFor(n: UserNotification): String = when (n.type?.uppercase()) {
    "RESPONSE_ACCEPTED", "PROVIDER_ACCEPTED" -> "✅"
    "RESPONSE_REJECTED", "RESPONSE_DECLINED", "PROVIDER_REJECTED" -> "⚠️"
    "NEW_ORDER" -> "📥"
    "NEW_RESPONSE" -> "💌"
    "ORDER_CANCELED" -> "✕"
    "ORDER_STARTED" -> "🔧"
    "ORDER_COMPLETED" -> "🏁"
    "TEST" -> "🧪"
    else -> when {
        n.title.contains("order", true) -> "📦"
        n.title.contains("offer", true) || n.title.contains("deal", true) -> "🏷️"
        n.title.contains("message", true) -> "💬"
        n.title.contains("respon", true) || n.title.contains("proposal", true) -> "💌"
        else -> "🔔"
    }
}

/**
 * Where a notification leads. Provider-side events open the order as a seller sees it; customer-side events open
 * the order detail. Only an order id is ever in a notification, never contact details.
 */
private fun openTarget(n: UserNotification, navigator: Navigator, model: NotificationsViewModel) {
    val orderId = n.orderId?.toInt()
    when (n.type?.uppercase()) {
        "NEW_ORDER", "RESPONSE_ACCEPTED", "RESPONSE_DECLINED", "RESPONSE_REJECTED", "ORDER_CANCELED", "ORDER_COMPLETED" ->
            orderId?.let { navigator.push(RespondToOrderScreen(it)) }
        "NEW_RESPONSE", "ORDER_STARTED" -> orderId?.let { navigator.push(OrderDetailScreen(it)) }
        "PROVIDER_ACCEPTED" -> model.onEvent(NotificationsEvent.RefreshRole)
    }
}

class NotificationsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<NotificationsViewModel>()
        val state by model.state.collectAsState()

        LaunchedEffect(state.becameSeller) {
            if (state.becameSeller) navigator.replaceAll(ProviderDashboardScreen())
        }
        val unread = state.notifications.count { !it.isRead }

        Div(attrs = { classes("space-y-6") }) {
            Div(attrs = { classes("flex", "items-start", "justify-between", "gap-3") }) {
                Div {
                    H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Notifications") }
                    if (!state.isLoading && state.error == null && state.notifications.isNotEmpty()) {
                        P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) {
                            Text(if (unread == 0) "You're all caught up" else "$unread unread")
                        }
                    }
                }
                if (unread > 0) GhostButton("Mark all as read", icon = "✓") { model.onEvent(NotificationsEvent.MarkAllRead) }
            }
            when {
                state.isLoading -> LoadingSkeletons(4, "h-16")
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                state.notifications.isEmpty() ->
                    EmptyState("🔔", "Nothing new yet", "We'll let you know the moment someone replies.")
                else -> {
                    Div(attrs = { classes("space-y-2", "stagger") }) {
                        state.notifications.forEach { n ->
                            Div(attrs = {
                                style { property("cursor", if (n.isRead) "default" else "pointer") }
                                onClick {
                                    if (!n.isRead) model.onEvent(NotificationsEvent.MarkRead(n.id))
                                    openTarget(n, navigator, model)
                                }
                            }) {
                                Card(classes = "flex items-start gap-4 px-5 py-4 ${if (n.isRead) "" else "border-brand-subtle"}") {
                                    Div(attrs = {
                                        classes(
                                            "w-10", "h-10", "rounded-full", "flex-shrink-0", "flex", "items-center",
                                            "justify-center", "text-lg", if (n.isRead) "bg-surface-secondary" else "bg-brand-softer"
                                        )
                                    }) { Text(iconFor(n)) }
                                    Div(attrs = { classes("flex-1", "min-w-0") }) {
                                        Div(attrs = { classes("flex", "items-center", "gap-2") }) {
                                            P(attrs = { classes("text-heading", "text-sm", if (n.isRead) "font-medium" else "font-semibold") }) { Text(n.title) }
                                            if (!n.isRead) Span(attrs = {
                                                classes("w-2", "h-2", "rounded-full", "bg-brand", "flex-shrink-0")
                                                attr("aria-label", "Unread")
                                            }) {}
                                        }
                                        if (n.body.isNotBlank()) P(attrs = { classes("text-sm", "text-body", "mt-0.5") }) { Text(n.body) }
                                        if (n.createdAt.isNotBlank()) P(attrs = { classes("text-xs", "text-body-subtle", "mt-1") }) {
                                            Text(n.createdAt.take(16).replace("T", " "))
                                        }
                                    }
                                    Button(attrs = {
                                        classes("p-2", "min-h-11", "text-body-subtle", "hover:text-fg-danger", "transition-colors")
                                        attr("aria-label", "Delete notification")
                                        style { property("background", "none"); property("border", "none"); property("cursor", "pointer") }
                                        onClick { it.stopPropagation(); model.onEvent(NotificationsEvent.Delete(n.id)) }
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
