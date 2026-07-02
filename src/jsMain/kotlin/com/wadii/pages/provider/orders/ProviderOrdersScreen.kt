package com.wadii.pages.provider.orders

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.pages.provider.respond.RespondToOrderScreen
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.LoadingScreen
import org.jetbrains.compose.web.dom.*

class ProviderOrdersScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<ProviderOrdersViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Incoming Orders") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                state.orders.isEmpty() -> EmptyState("📦", "No orders available yet.")
                else -> {
                    Div(attrs = { classes("space-y-3") }) {
                        state.orders.forEach { order ->
                            Div(attrs = {
                                style { property("cursor", "pointer") }
                                onClick { navigator.push(RespondToOrderScreen(order.id)) }
                            }) {
                                Card(classes = "p-5 hover:shadow-neu-md active:shadow-neu-inset transition-all") {
                                    Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                                        Div {
                                            P(attrs = { classes("font-semibold", "text-heading") }) { Text("Order #${order.id} — ${order.carModelYear}") }
                                            P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) { Text(order.comment.take(80)) }
                                            order.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                                Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                                    svcs.forEach { s -> Badge(s.name, variant = BadgeVariant.Brand, pill = true) }
                                                }
                                            }
                                        }
                                        Div(attrs = { classes("text-right") }) {
                                            P(attrs = { classes("text-xs", "text-body-subtle") }) { Text(order.date.take(10)) }
                                            P(attrs = { classes("text-xs", "text-body-subtle", "mt-1") }) { Text("${order.responses?.size ?: 0} response(s)") }
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
}
