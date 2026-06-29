package com.wadii.pages.provider.respond

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.pages.provider.orders.ProviderOrdersScreen
import com.wadii.ui.LoadingScreen
import com.wadii.ui.Spinner
import com.wadii.ui.TextArea
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf

class RespondToOrderScreen(val orderId: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<RespondToOrderViewModel> { parametersOf(orderId) }
        val state by model.state.collectAsState()

        if (state.submitted) {
            LaunchedEffect(Unit) { navigator.replaceAll(ProviderOrdersScreen()) }
            return
        }

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
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                        H1(attrs = { classes("text-xl", "font-bold", "text-slate-800", "mb-4") }) { Text("Order #${o.id}") }
                        Div(attrs = { classes("space-y-2", "text-sm") }) {
                            P { Span(attrs = { classes("text-slate-500") }) { Text("Vehicle: ") }; Span(attrs = { classes("font-medium") }) { Text(o.carModelYear) } }
                            P { Span(attrs = { classes("text-slate-500") }) { Text("Description: ") }; Text(o.comment) }
                            o.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                    svcs.forEach { s -> Span(attrs = { classes("px-2", "py-0.5", "bg-amber-50", "text-amber-700", "text-xs", "rounded-full") }) { Text(s.name) } }
                                }
                            }
                            o.spareParts?.takeIf { it.isNotEmpty() }?.let { parts ->
                                Div(attrs = { classes("mt-2") }) {
                                    P(attrs = { classes("text-slate-500", "mb-1") }) { Text("Spare parts:") }
                                    parts.forEach { sp -> P(attrs = { classes("text-slate-700") }) { Text("• ${sp.sparePartName}") } }
                                }
                            }
                        }
                    }
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                        H2(attrs = { classes("font-semibold", "text-slate-800", "mb-4") }) { Text("Submit Response") }
                        Div(attrs = { classes("space-y-5") }) {
                            TextArea("Your Comment", state.comment, "Describe your service offer, timeline…", 4) {
                                model.onEvent(RespondToOrderEvent.SetComment(it))
                            }
                            if (state.prices.isNotEmpty() && o.spareParts != null) {
                                Div {
                                    P(attrs = { classes("text-sm", "font-medium", "text-slate-700", "mb-2") }) { Text("Spare Parts Pricing") }
                                    Div(attrs = { classes("space-y-2") }) {
                                        o.spareParts!!.forEachIndexed { i, sp ->
                                            Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                                                Span(attrs = { classes("flex-1", "text-sm", "text-slate-700") }) { Text(sp.sparePartName) }
                                                Div(attrs = { classes("relative", "w-32") }) {
                                                    Span(attrs = { classes("absolute", "left-3", "top-1/2", "-translate-y-1/2", "text-slate-400", "text-sm") }) { Text("$") }
                                                    Input(type = InputType.Text, attrs = {
                                                        classes("w-full", "pl-7", "pr-3", "py-2", "border", "border-slate-300", "rounded-lg", "text-sm", "focus:outline-none", "focus:ring-2", "focus:ring-amber-400")
                                                        attr("placeholder", "0.00"); attr("type", "number"); attr("min", "0"); attr("step", "0.01")
                                                        attr("value", state.prices.getOrNull(i)?.second ?: "")
                                                        onInput { model.onEvent(RespondToOrderEvent.SetPrice(i, it.value)) }
                                                    })
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Button(attrs = {
                                classes("w-full", "py-3", "bg-amber-500", "hover:bg-amber-600", "text-white", "font-semibold", "rounded-xl", "disabled:opacity-60", "transition-colors", "flex", "items-center", "justify-center", "gap-2")
                                attr("type", "button")
                                onClick { model.onEvent(RespondToOrderEvent.Submit(orderId)) }
                                if (state.submitting) disabled()
                            }) { if (state.submitting) Spinner() else Text("Submit Response") }
                        }
                    }
                }
            }
        }
    }
}
