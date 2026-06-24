package com.wadii.pages.provider

import androidx.compose.runtime.*
import com.wadii.api.apiGetMyProvider
import com.wadii.api.apiGetProviderOrders
import com.wadii.model.Order
import com.wadii.model.Provider
import com.wadii.router.Route
import com.wadii.state.AppState
import com.wadii.api.to1dp
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.StatCard
import org.jetbrains.compose.web.dom.*

@Composable
fun ProviderDashboardPage() {
    var provider by remember { mutableStateOf<Provider?>(null) }
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        provider = apiGetMyProvider()
        orders = apiGetProviderOrders()
        loading = false
    }

    Div(attrs = { classes("space-y-8") }) {
        Div {
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Provider Dashboard") }
            provider?.let { P(attrs = { classes("text-slate-500") }) { Text(it.name) } }
        }

        if (loading) {
            Div(attrs = { classes("grid", "grid-cols-2", "md:grid-cols-4", "gap-4") }) {
                repeat(4) { Div(attrs = { classes("h-28", "bg-white", "rounded-2xl", "animate-pulse") }) {} }
            }
        } else {
            Div(attrs = { classes("grid", "grid-cols-2", "md:grid-cols-4", "gap-4") }) {
                StatCard("Rating", "${(provider?.rate ?: 0.0).to1dp()}", "⭐", "bg-amber-50")
                StatCard("Followers", "${provider?.followersCount ?: 0}", "👥", "bg-blue-50")
                StatCard("Total Orders", "${orders.size}", "📦", "bg-green-50")
                val pending = orders.count { o -> o.responses?.any { it.state == "PENDING" } == true }
                StatCard("Pending", "$pending", "⏳", "bg-orange-50")
            }

            provider?.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                    H2(attrs = { classes("font-semibold", "text-slate-800", "mb-3") }) { Text("My Services") }
                    Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                        svcs.forEach { s ->
                            Span(attrs = { classes("px-3", "py-1.5", "bg-amber-50", "text-amber-700",
                                "rounded-full", "text-sm") }) { Text(s.name) }
                        }
                    }
                }
            }

            // Recent Orders
            Div {
                Div(attrs = { classes("flex", "items-center", "justify-between", "mb-3") }) {
                    H2(attrs = { classes("font-semibold", "text-slate-800") }) { Text("Recent Orders") }
                    Button(attrs = {
                        classes("text-sm", "text-amber-600", "hover:underline")
                        onClick { AppState.navigate(Route.PROVIDER_ORDERS) }
                    }) { Text("View all") }
                }
                if (orders.isEmpty()) {
                    Div(attrs = { classes("bg-white", "rounded-xl", "p-8", "text-center",
                        "text-slate-500", "text-sm") }) { Text("No orders yet.") }
                } else {
                    Div(attrs = { classes("space-y-3") }) {
                        orders.take(5).forEach { order ->
                            Div(attrs = {
                                classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-4",
                                    "hover:shadow-md", "transition-shadow", "cursor-pointer")
                                onClick { AppState.navigate(Route.RESPOND_ORDER, order.id.toString()) }
                            }) {
                                Div(attrs = { classes("flex", "items-center", "justify-between") }) {
                                    Div {
                                        P(attrs = { classes("font-medium", "text-slate-800") }) {
                                            Text("Order #${order.id} — ${order.carModelYear}")
                                        }
                                        P(attrs = { classes("text-sm", "text-slate-500") }) {
                                            Text(order.comment.take(60))
                                        }
                                    }
                                    P(attrs = { classes("text-xs", "text-slate-400") }) {
                                        Text(order.date.take(10))
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
