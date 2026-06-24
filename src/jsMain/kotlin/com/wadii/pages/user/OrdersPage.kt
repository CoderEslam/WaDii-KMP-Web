package com.wadii.pages.user

import androidx.compose.runtime.*
import com.wadii.screens.NewOrderScreen
import com.wadii.screens.OrderDetailScreen
import com.wadii.ui.LoadingScreen
import com.wadii.navigation.LocalNavigator
import com.wadii.navigation.currentOrThrow
import com.wadii.viewmodel.OrdersEvent
import com.wadii.viewmodel.OrdersScreenModel
import com.wadii.viewmodel.UiState
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.dom.*

@Composable
fun OrdersPage() {
    val navigator = LocalNavigator.currentOrThrow
    val model = rememberScreenModel { OrdersScreenModel() }

    Div(attrs = { classes("space-y-6") }) {
        Div(attrs = { classes("flex", "items-center", "justify-between") }) {
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("My Orders") }
            Button(attrs = {
                classes("flex", "items-center", "gap-2", "px-4", "py-2", "bg-amber-500",
                    "text-white", "rounded-xl", "text-sm", "font-medium", "hover:bg-amber-600")
                onClick { navigator.push(NewOrderScreen) }
            }) { Text("+ New Order") }
        }

        when (val s = model.state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) { Text(s.message) }
            is UiState.Success -> {
                val orders = s.data.orders
                if (orders.isEmpty()) {
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                        P(attrs = { classes("text-5xl", "mb-3") }) { Text("📦") }
                        P(attrs = { classes("text-slate-500", "mb-4") }) { Text("You haven't placed any orders yet.") }
                        Button(attrs = {
                            classes("px-5", "py-2", "bg-amber-500", "text-white", "font-medium", "rounded-lg", "hover:bg-amber-600", "text-sm")
                            onClick { navigator.push(NewOrderScreen) }
                        }) { Text("Create your first order") }
                    }
                } else {
                    Div(attrs = { classes("space-y-3") }) {
                        orders.forEach { order ->
                            Div(attrs = {
                                classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5",
                                    "hover:shadow-md", "transition-shadow", "cursor-pointer")
                                onClick { navigator.push(OrderDetailScreen(order.id)) }
                            }) {
                                Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                                    Div {
                                        P(attrs = { classes("font-semibold", "text-slate-800") }) { Text("Order #${order.id} — ${order.carModelYear}") }
                                        P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(order.comment.take(80)) }
                                        order.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                            Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                                svcs.forEach { s ->
                                                    Span(attrs = { classes("text-xs", "bg-slate-100", "text-slate-600", "px-2", "py-0.5", "rounded-full") }) { Text(s.name) }
                                                }
                                            }
                                        }
                                    }
                                    Div(attrs = { classes("text-right") }) {
                                        P(attrs = { classes("text-xs", "text-slate-400") }) { Text(order.date.take(10)) }
                                        val latestState = order.responses?.firstOrNull()?.state
                                        if (latestState != null) {
                                            val active = when (latestState) {
                                                "ACCEPT" -> arrayOf("bg-green-50", "text-green-700")
                                                "CANCEL" -> arrayOf("bg-red-50", "text-red-700")
                                                else -> arrayOf("bg-yellow-50", "text-yellow-700")
                                            }
                                            val label = when (latestState) { "ACCEPT" -> "Accepted"; "CANCEL" -> "Declined"; else -> "Pending" }
                                            Span(attrs = { classes(*active, "text-xs", "px-2", "py-0.5", "rounded-full", "font-medium", "mt-1", "inline-block") }) { Text(label) }
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
