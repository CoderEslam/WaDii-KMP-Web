package com.wadii.pages.provider.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.data.api.to1dp
import com.wadii.pages.provider.orders.ProviderOrdersScreen
import com.wadii.pages.provider.respond.RespondToOrderScreen
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
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Provider Dashboard") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) { Text(state.error!!) }
                else -> {
                    val provider = state.provider
                    val orders = state.orders
                    provider?.let { P(attrs = { classes("text-slate-500", "-mt-6") }) { Text(it.name) } }

                    Div(attrs = { classes("grid", "grid-cols-2", "md:grid-cols-4", "gap-4") }) {
                        StatCard("Rating", "${(provider?.rate ?: 0.0).to1dp()}", "⭐", "bg-amber-50")
                        StatCard("Followers", "${provider?.followersCount ?: 0}", "👥", "bg-blue-50")
                        StatCard("Total Orders", "${orders.size}", "📦", "bg-green-50")
                        StatCard("Pending", "Pending", "⏳", "bg-orange-50")
                    }

                    provider?.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                            H2(attrs = { classes("font-semibold", "text-slate-800", "mb-3") }) { Text("My Services") }
                            Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                                svcs.forEach { s ->
                                    Span(attrs = { classes("px-3", "py-1.5", "bg-amber-50", "text-amber-700", "rounded-full", "text-sm") }) { Text(s.name) }
                                }
                            }
                        }
                    }

                    Div {
                        Div(attrs = { classes("flex", "items-center", "justify-between", "mb-3") }) {
                            H2(attrs = { classes("font-semibold", "text-slate-800") }) { Text("Recent Orders") }
                            Button(attrs = {
                                classes("text-sm", "text-amber-600", "hover:underline")
                                onClick { navigator.replaceAll(ProviderOrdersScreen()) }
                            }) { Text("View all") }
                        }
                        if (orders.isEmpty()) {
                            Div(attrs = { classes("bg-white", "rounded-xl", "p-8", "text-center", "text-slate-500", "text-sm") }) { Text("No orders yet.") }
                        } else {
                            Div(attrs = { classes("space-y-3") }) {
                                orders.take(5).forEach { order ->
                                    Div(attrs = {
                                        classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-4", "hover:shadow-md", "transition-shadow", "cursor-pointer")
                                        onClick { navigator.push(RespondToOrderScreen(order.id)) }
                                    }) {
                                        Div(attrs = { classes("flex", "items-center", "justify-between") }) {
                                            Div {
                                                P(attrs = { classes("font-medium", "text-slate-800") }) { Text("Order #${order.id} — ${order.carModelYear}") }
                                                P(attrs = { classes("text-sm", "text-slate-500") }) { Text(order.comment.take(60)) }
                                            }
                                            P(attrs = { classes("text-xs", "text-slate-400") }) { Text(order.date.take(10)) }
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

