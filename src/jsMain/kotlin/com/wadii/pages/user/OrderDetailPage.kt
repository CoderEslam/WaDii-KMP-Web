package com.wadii.pages.user

import androidx.compose.runtime.*
import com.wadii.ui.LoadingScreen
import com.wadii.navigation.LocalNavigator
import com.wadii.navigation.currentOrThrow
import com.wadii.viewmodel.OrderDetailEvent
import com.wadii.viewmodel.OrderDetailScreenModel
import com.wadii.viewmodel.UiState
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.dom.*

@Composable
fun OrderDetailPage(orderId: Long) {
    val navigator = LocalNavigator.currentOrThrow
    val model = rememberScreenModel { OrderDetailScreenModel() }

    LaunchedEffect(orderId) { model.onEvent(OrderDetailEvent.Load(orderId)) }

    Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
        Button(attrs = {
            classes("text-sm", "text-slate-500", "hover:text-slate-700")
            onClick { navigator.pop() }
        }) { Text("← Back to orders") }

        when (val s = model.state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) { Text(s.message) }
            is UiState.Success -> {
                val o = s.data.order
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
                                    val stateClasses = when (resp.state) {
                                        "ACCEPT" -> arrayOf("bg-green-50", "text-green-700")
                                        "CANCEL" -> arrayOf("bg-red-50", "text-red-700")
                                        else -> arrayOf("bg-yellow-50", "text-yellow-700")
                                    }
                                    val label = when (resp.state) { "ACCEPT" -> "Accepted"; "CANCEL" -> "Declined"; else -> "Pending" }
                                    Span(attrs = { classes(*stateClasses, "text-xs", "px-2", "py-0.5", "rounded-full", "font-medium") }) { Text(label) }
                                }
                                P(attrs = { classes("text-sm", "text-slate-700", "mb-3") }) { Text(resp.comment) }
                                resp.sparePartsPrice?.takeIf { it.isNotEmpty() }?.let { prices ->
                                    Div(attrs = { classes("border-t", "border-slate-100", "pt-3", "space-y-1") }) {
                                        P(attrs = { classes("text-xs", "font-medium", "text-slate-500", "mb-2") }) { Text("💲 Pricing") }
                                        prices.forEach { spp ->
                                            Div(attrs = { classes("flex", "justify-between", "text-sm") }) {
                                                Span(attrs = { classes("text-slate-600") }) { Text(spp.spareParts?.sparePartName ?: "") }
                                                Span(attrs = { classes("font-medium", "text-slate-800") }) { Text("$${spp.price}") }
                                            }
                                        }
                                    }
                                }
                                if (resp.state == "PENDING") {
                                    Div(attrs = { classes("flex", "gap-2", "mt-4") }) {
                                        Button(attrs = {
                                            classes("flex", "items-center", "gap-1.5", "px-4", "py-2", "bg-green-500", "text-white", "text-sm", "font-medium", "rounded-lg", "hover:bg-green-600")
                                            onClick { model.onEvent(OrderDetailEvent.AcceptResponse(resp.id, orderId)) }
                                        }) { Text("✓ Accept") }
                                        Button(attrs = {
                                            classes("flex", "items-center", "gap-1.5", "px-4", "py-2", "bg-red-100", "text-red-600", "text-sm", "font-medium", "rounded-lg", "hover:bg-red-200")
                                            onClick { model.onEvent(OrderDetailEvent.DeclineResponse(resp.id, orderId)) }
                                        }) { Text("✕ Decline") }
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

@Composable
private fun OrderInfoRow(label: String, value: String) {
    Div {
        P(attrs = { classes("text-xs", "font-medium", "text-slate-500", "uppercase", "tracking-wide") }) { Text(label) }
        P(attrs = { classes("font-medium", "text-slate-800", "mt-0.5") }) { Text(value) }
    }
}
