package com.wadii.pages.provider

import androidx.compose.runtime.*
import com.wadii.screens.RespondToOrderScreen
import com.wadii.ui.LoadingScreen
import com.wadii.navigation.LocalNavigator
import com.wadii.navigation.currentOrThrow
import com.wadii.viewmodel.ProviderOrdersEvent
import com.wadii.viewmodel.ProviderOrdersScreenModel
import com.wadii.viewmodel.UiState
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.dom.*

@Composable
fun ProviderOrdersPage() {
    val navigator = LocalNavigator.currentOrThrow
    val model = rememberScreenModel { ProviderOrdersScreenModel() }

    Div(attrs = { classes("space-y-6") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Incoming Orders") }

        when (val s = model.state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) { Text(s.message) }
            is UiState.Success -> {
                val orders = s.data.orders
                if (orders.isEmpty()) {
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                        P(attrs = { classes("text-5xl", "mb-3") }) { Text("📦") }
                        P(attrs = { classes("text-slate-500") }) { Text("No orders available yet.") }
                    }
                } else {
                    Div(attrs = { classes("space-y-3") }) {
                        orders.forEach { order ->
                            Div(attrs = {
                                classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5",
                                    "hover:shadow-md", "transition-shadow", "cursor-pointer")
                                onClick { navigator.push(RespondToOrderScreen(order.id)) }
                            }) {
                                Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                                    Div {
                                        P(attrs = { classes("font-semibold", "text-slate-800") }) { Text("Order #${order.id} — ${order.carModelYear}") }
                                        P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(order.comment.take(80)) }
                                        order.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                            Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                                svcs.forEach { s ->
                                                    Span(attrs = { classes("text-xs", "bg-amber-50", "text-amber-700", "px-2", "py-0.5", "rounded-full") }) { Text(s.name) }
                                                }
                                            }
                                        }
                                    }
                                    Div(attrs = { classes("text-right") }) {
                                        P(attrs = { classes("text-xs", "text-slate-400") }) { Text(order.date.take(10)) }
                                        P(attrs = { classes("text-xs", "text-slate-500", "mt-1") }) { Text("${order.responses?.size ?: 0} response(s)") }
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
