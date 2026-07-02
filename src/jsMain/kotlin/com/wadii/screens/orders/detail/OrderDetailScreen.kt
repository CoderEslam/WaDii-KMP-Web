package com.wadii.screens.orders.detail

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.BackButton
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.LoadingScreen
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf

class OrderDetailScreen(val orderId: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<OrderDetailViewModel> { parametersOf(orderId) }
        val state by model.state.collectAsState()

        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            BackButton("Back to orders") { navigator.pop() }
            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)

                state.order != null -> {
                    val o = state.order!!
                    Card(classes = "p-6") {
                        Div(attrs = { classes("space-y-4") }) {
                            Div(attrs = { classes("flex", "justify-between", "items-start") }) {
                                H1(attrs = { classes("text-xl", "font-semibold", "text-heading") }) { Text("Order #${o.id}") }
                                P(attrs = { classes("text-xs", "text-body-subtle") }) { Text(o.date.take(10)) }
                            }
                            OrderInfoRow("Vehicle", o.carModelYear)
                            OrderInfoRow("Description", o.comment)
                            o.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                Div {
                                    P(attrs = { classes("text-xs", "font-medium", "text-body-subtle", "uppercase", "tracking-wide", "mb-1") }) { Text("Services") }
                                    Div(attrs = { classes("flex", "flex-wrap", "gap-1") }) {
                                        svcs.forEach { s -> Badge(s.name, variant = BadgeVariant.Brand, pill = true) }
                                    }
                                }
                            }
                            o.spareParts?.takeIf { it.isNotEmpty() }?.let { parts ->
                                Div {
                                    P(attrs = { classes("text-xs", "font-medium", "text-body-subtle", "uppercase", "tracking-wide", "mb-1") }) { Text("Spare Parts") }
                                    parts.forEach { sp ->
                                        P(attrs = { classes("text-sm", "text-body") }) { Text("• ${sp.sparePartName}") }
                                    }
                                }
                            }
                        }
                    }

                    H2(attrs = { classes("font-semibold", "text-heading") }) { Text("Responses (${o.responses?.size ?: 0})") }
                    if (o.responses.isNullOrEmpty()) {
                        EmptyState("💬", "No responses yet. Providers will respond soon.")
                    } else {
                        Div(attrs = { classes("space-y-3") }) {
                            o.responses!!.forEach { resp ->
                                Card(classes = "p-5") {
                                    P(attrs = { classes("font-medium", "text-heading", "mb-3") }) { Text("👤 ${resp.provider?.name ?: "Provider"}") }
                                    P(attrs = { classes("text-sm", "text-body") }) { Text(resp.comment) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderInfoRow(label: String, value: String) {
    Div {
        P(attrs = { classes("text-xs", "font-medium", "text-body-subtle", "uppercase", "tracking-wide") }) { Text(label) }
        P(attrs = { classes("font-medium", "text-heading", "mt-0.5") }) { Text(value) }
    }
}
