package com.wadii.pages.provider.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.data.api.to1dp
import com.wadii.pages.provider.orders.ProviderOrdersScreen
import com.wadii.pages.provider.respond.RespondToOrderScreen
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.LoadingScreen
import com.wadii.ui.StatCard
import cafe.adriel.voyager.koin.koinScreenModel
import org.jetbrains.compose.web.dom.*

class ProviderDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<ProviderDashboardViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-8") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Seller Dashboard") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    val provider = state.provider
                    val orders = state.orders
                    provider?.let { P(attrs = { classes("text-body-subtle", "-mt-6") }) { Text(it.name) } }

                    Div(attrs = { classes("grid", "grid-cols-2", "md:grid-cols-4", "gap-4") }) {
                        StatCard("Rating", "${(provider?.rate ?: 0.0).to1dp()}", "⭐")
                        StatCard("Followers", "${provider?.followersCount ?: 0}", "👥")
                        StatCard("Total Orders", "${orders.size}", "📦")
                        StatCard("Pending", "Pending", "⏳")
                    }

                    provider?.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                        Card(classes = "p-6") {
                            H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("My Services") }
                            Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                                svcs.forEach { s -> Badge(s.name, variant = BadgeVariant.Brand, pill = true) }
                            }
                        }
                    }

                    Div {
                        Div(attrs = { classes("flex", "items-center", "justify-between", "mb-3") }) {
                            H2(attrs = { classes("font-semibold", "text-heading") }) { Text("Recent Orders") }
                            GhostButton("View all") { navigator.replaceAll(ProviderOrdersScreen()) }
                        }
                        if (orders.isEmpty()) {
                            EmptyState("📦", "No orders yet.")
                        } else {
                            Div(attrs = { classes("space-y-3") }) {
                                orders.take(5).forEach { order ->
                                    Div(attrs = {
                                        style { property("cursor", "pointer") }
                                        onClick { navigator.push(RespondToOrderScreen(order.id)) }
                                    }) {
                                        Card(classes = "p-4 hover:shadow-neu-md active:shadow-neu-inset transition-all") {
                                            Div(attrs = { classes("flex", "items-center", "justify-between") }) {
                                                Div {
                                                    P(attrs = { classes("font-medium", "text-heading") }) { Text("Order #${order.id} — ${order.carModelYear}") }
                                                    P(attrs = { classes("text-sm", "text-body-subtle") }) { Text(order.comment.take(60)) }
                                                }
                                                P(attrs = { classes("text-xs", "text-body-subtle") }) { Text(order.date.take(10)) }
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
}
