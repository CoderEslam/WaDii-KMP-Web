package com.wadii.screens.orders.new

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.screens.orders.list.OrdersScreen
import com.wadii.ui.InputField
import com.wadii.ui.LoadingScreen
import com.wadii.ui.Spinner
import com.wadii.ui.TextArea
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

class NewOrderScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<NewOrderViewModel>()
        val state by model.state.collectAsState()

        if (state.submitted) {
            LaunchedEffect(Unit) { navigator.replaceAll(OrdersScreen()) }
            return
        }

        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                Button(attrs = {
                    classes("text-slate-500", "hover:text-slate-700", "text-sm")
                    onClick { navigator.replaceAll(OrdersScreen()) }
                }) { Text("← Back") }
                H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("New Order") }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) { Text(state.error!!) }
                else -> {
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                        Div(attrs = { classes("space-y-5") }) {
                            InputField("Vehicle (Make, Model, Year)", state.carModelYear, "Toyota Camry 2022", required = true) {
                                model.onEvent(NewOrderEvent.SetCar(it))
                            }
                            TextArea("Description", state.comment, "Describe what you need…", 4) {
                                model.onEvent(NewOrderEvent.SetComment(it))
                            }

                            if (state.services.isNotEmpty()) {
                                Div {
                                    P(attrs = { classes("text-sm", "font-medium", "text-slate-700", "mb-2") }) { Text("Services Needed") }
                                    Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                                        state.services.forEach { svc ->
                                            val sel = svc.id in state.selectedServices
                                            Button(attrs = {
                                                attr("type", "button")
                                                classes("px-3", "py-1.5", "rounded-full", "text-sm", "transition-colors")
                                                if (sel) classes("bg-amber-500", "text-white")
                                                else classes("bg-slate-100", "text-slate-700", "hover:bg-slate-200")
                                                onClick { model.onEvent(NewOrderEvent.ToggleService(svc.id)) }
                                            }) { Text(svc.name) }
                                        }
                                    }
                                }
                            }

                            Div {
                                P(attrs = { classes("text-sm", "font-medium", "text-slate-700", "mb-2") }) { Text("Spare Parts Needed") }
                                Div(attrs = { classes("space-y-2") }) {
                                    state.spareParts.forEachIndexed { i, part ->
                                        Div(attrs = { classes("flex", "gap-2") }) {
                                            Input(type = InputType.Text, attrs = {
                                                classes("flex-1", "px-4", "py-2", "border", "border-slate-300", "rounded-lg", "text-sm", "focus:outline-none", "focus:ring-2", "focus:ring-amber-400")
                                                attr("placeholder", "e.g. Brake pad")
                                                attr("value", part)
                                                onInput { model.onEvent(NewOrderEvent.SetSparePart(i, it.value)) }
                                            })
                                            if (state.spareParts.size > 1) {
                                                Button(attrs = {
                                                    attr("type", "button")
                                                    classes("px-3", "py-2", "text-red-400", "hover:text-red-600", "text-sm")
                                                    onClick { model.onEvent(NewOrderEvent.RemoveSparePart(i)) }
                                                }) { Text("✕") }
                                            }
                                        }
                                    }
                                    Button(attrs = {
                                        attr("type", "button")
                                        classes("text-sm", "text-amber-600", "hover:underline")
                                        onClick { model.onEvent(NewOrderEvent.AddSparePart) }
                                    }) { Text("+ Add spare part") }
                                }
                            }

                            Button(attrs = {
                                classes("w-full", "py-3", "bg-amber-500", "hover:bg-amber-600", "text-white", "font-semibold", "rounded-xl", "transition-colors", "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2")
                                attr("type", "button")
                                onClick { model.onEvent(NewOrderEvent.Submit) }
                                if (state.submitting) disabled()
                            }) { if (state.submitting) Spinner() else Text("Submit Order") }
                        }
                    }
                }
            }
        }
    }
}
