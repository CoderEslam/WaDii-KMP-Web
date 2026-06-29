package com.wadii.screens.orders.detail

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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
            Button(attrs = {
                classes("text-sm", "text-slate-500", "hover:text-slate-700")
                onClick { navigator.pop() }
            }) { Text("← Back to orders") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) { Text(state.error!!) }
                state.order != null -> {
                    val o = state.order!!
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm", "space-y-4") }) {
                        Div(attrs = { classes("flex", "justify-between", "items-start") }) {
                            H1(attrs = { classes("text-xl", "font-bold", "text-slate-800") }) { Text("Order #${o.id}") }
                            P(attrs = { classes("text-xs", "text-slate-400") }) { Text(o.date.take(10)) }
                        }
                        OrderInfoRow("Vehicle", o.carModelYear)
                        OrderInfoRow("Description", o.comment)
                        o.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                            Div {
                                P(attrs = { classes("text-xs", "font-medium", "text-slate-500", "uppercase", "tracking-wide", "mb-1") }) { Text("Services") }
                                Div(attrs = { classes("flex", "flex-wrap", "gap-1") }) {
                                    svcs.forEach { s -> Span(attrs = { classes("px-2", "py-0.5", "bg-amber-50", "text-amber-700", "text-sm", "rounded-full") }) { Text(s.name) } }
                                }
                            }
                        }
                        o.spareParts?.takeIf { it.isNotEmpty() }?.let { parts ->
                            Div {
                                P(attrs = { classes("text-xs", "font-medium", "text-slate-500", "uppercase", "tracking-wide", "mb-1") }) { Text("Spare Parts") }
                                parts.forEach { sp -> P(attrs = { classes("text-sm", "text-slate-700") }) { Text("• ${sp.sparePartName}") } }
                            }
                        }
                    }

                    H2(attrs = { classes("font-semibold", "text-slate-800") }) { Text("Responses (${o.responses?.size ?: 0})") }
                    if (o.responses.isNullOrEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-xl", "p-8", "text-center", "text-slate-500", "text-sm") }) { Text("No responses yet. Providers will respond soon.") }
                    } else {
                        Div(attrs = { classes("space-y-3") }) {
                            o.responses!!.forEach { resp ->
                                Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5") }) {
                                    Div(attrs = { classes("flex", "items-start", "justify-between", "mb-3") }) {
                                        P(attrs = { classes("font-medium", "text-slate-800") }) { Text("👤 ${resp.provider?.name ?: "Provider"}") }
                                    }
                                    P(attrs = { classes("text-sm", "text-slate-700", "mb-3") }) { Text(resp.comment) }
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
        P(attrs = { classes("text-xs", "font-medium", "text-slate-500", "uppercase", "tracking-wide") }) { Text(label) }
        P(attrs = { classes("font-medium", "text-slate-800", "mt-0.5") }) { Text(value) }
    }
}
