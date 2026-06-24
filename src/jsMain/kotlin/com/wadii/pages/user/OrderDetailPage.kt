package com.wadii.pages.user

import androidx.compose.runtime.*
import com.wadii.api.apiAcceptResponse
import com.wadii.api.apiCancelResponse
import com.wadii.api.apiGetOrder
import com.wadii.model.Order
import com.wadii.router.Route
import com.wadii.state.AppState
import com.wadii.ui.LoadingSkeletons
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.*

@Composable
fun OrderDetailPage() {
    val orderId = AppState.routeParam?.toLongOrNull() ?: return
    var order by remember { mutableStateOf<Order?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val reload: () -> Unit = {
        loading = true
        scope.launch {
            order = apiGetOrder(orderId)
            loading = false
        }
    }

    LaunchedEffect(orderId) { reload() }

    Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
        Button(attrs = {
            classes("text-sm", "text-slate-500", "hover:text-slate-700")
            onClick { AppState.navigate(Route.ORDERS) }
        }) { Text("← Back to orders") }

        if (loading) {
            LoadingSkeletons(2)
        } else if (order == null) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) {
                Text("Order not found.")
            }
        } else {
            val o = order!!
            // Order Info
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm", "space-y-4") }) {
                Div(attrs = { classes("flex", "justify-between", "items-start") }) {
                    H1(attrs = { classes("text-xl", "font-bold", "text-slate-800") }) {
                        Text("Order #${o.id}")
                    }
                    P(attrs = { classes("text-xs", "text-slate-400") }) { Text(o.date.take(10)) }
                }
                InfoRow("Vehicle", o.carModelYear)
                InfoRow("Description", o.comment)
                o.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                    Div {
                        P(attrs = { classes("text-xs", "font-medium", "text-slate-500", "uppercase", "tracking-wide", "mb-1") }) {
                            Text("Services")
                        }
                        Div(attrs = { classes("flex", "flex-wrap", "gap-1") }) {
                            svcs.forEach { s ->
                                Span(attrs = { classes("px-2", "py-0.5", "bg-amber-50", "text-amber-700",
                                    "text-sm", "rounded-full") }) { Text(s.name) }
                            }
                        }
                    }
                }
                o.spareParts?.takeIf { it.isNotEmpty() }?.let { parts ->
                    Div {
                        P(attrs = { classes("text-xs", "font-medium", "text-slate-500", "uppercase", "tracking-wide", "mb-1") }) {
                            Text("Spare Parts")
                        }
                        parts.forEach { sp ->
                            P(attrs = { classes("text-sm", "text-slate-700") }) { Text("• ${sp.sparePartName}") }
                        }
                    }
                }
            }

            // Responses
            H2(attrs = { classes("font-semibold", "text-slate-800") }) {
                Text("Responses (${o.responses?.size ?: 0})")
            }
            if (o.responses.isNullOrEmpty()) {
                Div(attrs = { classes("bg-white", "rounded-xl", "p-8", "text-center", "text-slate-500", "text-sm") }) {
                    Text("No responses yet. Providers will respond soon.")
                }
            } else {
                Div(attrs = { classes("space-y-3") }) {
                    o.responses!!.forEach { resp ->
                        Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5") }) {
                            Div(attrs = { classes("flex", "items-start", "justify-between", "mb-3") }) {
                                P(attrs = { classes("font-medium", "text-slate-800") }) {
                                    Text("👤 ${resp.provider?.name ?: "Provider"}")
                                }
                                val (color, label) = when (resp.state) {
                                    "ACCEPT" -> "bg-green-50 text-green-700" to "Accepted"
                                    "CANCEL" -> "bg-red-50 text-red-700" to "Declined"
                                    else -> "bg-yellow-50 text-yellow-700" to "Pending"
                                }
                                Span(attrs = { classes(*color.split(" ").toTypedArray(),
                                    "text-xs", "px-2", "py-0.5", "rounded-full", "font-medium") }) { Text(label) }
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
                                        classes("flex", "items-center", "gap-1.5", "px-4", "py-2",
                                            "bg-green-500", "text-white", "text-sm", "font-medium",
                                            "rounded-lg", "hover:bg-green-600")
                                        onClick {
                                            scope.launch {
                                                if (apiAcceptResponse(resp.id)) {
                                                    AppState.toast("Response accepted!")
                                                    reload()
                                                } else {
                                                    AppState.toast("Failed to accept", true)
                                                }
                                            }
                                        }
                                    }) { Text("✓ Accept") }
                                    Button(attrs = {
                                        classes("flex", "items-center", "gap-1.5", "px-4", "py-2",
                                            "bg-red-100", "text-red-600", "text-sm", "font-medium",
                                            "rounded-lg", "hover:bg-red-200")
                                        onClick {
                                            scope.launch {
                                                if (apiCancelResponse(resp.id)) {
                                                    AppState.toast("Response declined")
                                                    reload()
                                                } else {
                                                    AppState.toast("Failed", true)
                                                }
                                            }
                                        }
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

@Composable
private fun InfoRow(label: String, value: String) {
    Div {
        P(attrs = { classes("text-xs", "font-medium", "text-slate-500", "uppercase", "tracking-wide") }) { Text(label) }
        P(attrs = { classes("font-medium", "text-slate-800", "mt-0.5") }) { Text(value) }
    }
}
