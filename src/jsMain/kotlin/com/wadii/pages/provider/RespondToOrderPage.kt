package com.wadii.pages.provider

import androidx.compose.runtime.*
import com.wadii.api.apiGetOrder
import com.wadii.api.apiInsertResponse
import com.wadii.model.Order
import com.wadii.router.Route
import com.wadii.state.AppState
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.Spinner
import com.wadii.ui.TextArea
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun RespondToOrderPage() {
    val orderId = AppState.routeParam?.toLongOrNull() ?: return
    var order by remember { mutableStateOf<Order?>(null) }
    var loading by remember { mutableStateOf(true) }
    var comment by remember { mutableStateOf("") }
    var prices by remember { mutableStateOf<List<Pair<Long, String>>>(emptyList()) }
    var submitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(orderId) {
        order = apiGetOrder(orderId)
        order?.spareParts?.let { parts ->
            prices = parts.map { it.id to "" }
        }
        loading = false
    }

    fun submit() {
        if (submitting) return
        submitting = true
        scope.launch {
            val body = mapOf(
                "comment" to comment,
                "order" to mapOf("id" to orderId),
                "sparePartsPrice" to prices
                    .filter { (_, price) -> price.isNotBlank() }
                    .map { (id, price) ->
                        mapOf("price" to price.toDoubleOrNull(), "spareParts" to mapOf("id" to id))
                    }
            )
            val resp = apiInsertResponse(body)
            submitting = false
            if (resp != null) {
                AppState.toast("Response submitted!")
                AppState.navigate(Route.PROVIDER_ORDERS)
            } else {
                AppState.toast("Failed to submit", true)
            }
        }
    }

    Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
        Button(attrs = {
            classes("text-sm", "text-slate-500", "hover:text-slate-700")
            onClick { AppState.navigate(Route.PROVIDER_ORDERS) }
        }) { Text("← Back to orders") }

        if (loading) { LoadingSkeletons(2) }
        else if (order == null) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) {
                Text("Order not found.")
            }
        } else {
            val o = order!!
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                H1(attrs = { classes("text-xl", "font-bold", "text-slate-800", "mb-4") }) {
                    Text("Order #${o.id}")
                }
                Div(attrs = { classes("space-y-2", "text-sm") }) {
                    P { Span(attrs = { classes("text-slate-500") }) { Text("Vehicle: ") }
                        Span(attrs = { classes("font-medium") }) { Text(o.carModelYear) } }
                    P { Span(attrs = { classes("text-slate-500") }) { Text("Description: ") }; Text(o.comment) }
                    o.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                        Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                            svcs.forEach { s ->
                                Span(attrs = { classes("px-2", "py-0.5", "bg-amber-50", "text-amber-700",
                                    "text-xs", "rounded-full") }) { Text(s.name) }
                            }
                        }
                    }
                    o.spareParts?.takeIf { it.isNotEmpty() }?.let { parts ->
                        Div(attrs = { classes("mt-2") }) {
                            P(attrs = { classes("text-slate-500", "mb-1") }) { Text("Spare parts requested:") }
                            parts.forEach { sp ->
                                P(attrs = { classes("text-slate-700") }) { Text("• ${sp.sparePartName}") }
                            }
                        }
                    }
                }
            }

            Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                H2(attrs = { classes("font-semibold", "text-slate-800", "mb-4") }) { Text("Submit Response") }
                Div(attrs = { classes("space-y-5") }) {
                    TextArea("Your Comment", comment, "Describe your service offer, timeline…", 4) { comment = it }

                    if (prices.isNotEmpty() && o.spareParts != null) {
                        Div {
                            P(attrs = { classes("text-sm", "font-medium", "text-slate-700", "mb-2") }) {
                                Text("Spare Parts Pricing")
                            }
                            Div(attrs = { classes("space-y-2") }) {
                                o.spareParts!!.forEachIndexed { i, sp ->
                                    Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                                        Span(attrs = { classes("flex-1", "text-sm", "text-slate-700") }) {
                                            Text(sp.sparePartName)
                                        }
                                        Div(attrs = { classes("relative", "w-32") }) {
                                            Span(attrs = { classes("absolute", "left-3", "top-1/2",
                                                "-translate-y-1/2", "text-slate-400", "text-sm") }) { Text("$") }
                                            Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
                                                classes("w-full", "pl-7", "pr-3", "py-2", "border",
                                                    "border-slate-300", "rounded-lg", "text-sm",
                                                    "focus:outline-none", "focus:ring-2", "focus:ring-amber-400")
                                                attr("placeholder", "0.00")
                                                attr("type", "number")
                                                attr("min", "0")
                                                attr("step", "0.01")
                                                attr("value", prices.getOrNull(i)?.second ?: "")
                                                onInput { ev ->
                                                    prices = prices.toMutableList().also { l ->
                                                        if (i < l.size) l[i] = l[i].first to ev.value
                                                    }
                                                }
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Button(attrs = {
                        classes("w-full", "py-3", "bg-amber-500", "hover:bg-amber-600",
                            "text-white", "font-semibold", "rounded-xl", "disabled:opacity-60",
                            "transition-colors", "flex", "items-center", "justify-center", "gap-2")
                        attr("type", "button")
                        onClick { submit() }
                        if (submitting) disabled()
                    }) { if (submitting) Spinner() else Text("Submit Response") }
                }
            }
        }
    }
}
