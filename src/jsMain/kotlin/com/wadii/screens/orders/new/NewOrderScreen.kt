package com.wadii.screens.orders.new

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.screens.orders.list.OrdersScreen
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.BackButton
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.InputField
import com.wadii.ui.LoadingScreen
import com.wadii.ui.PrimaryButton
import com.wadii.ui.TextArea
import org.jetbrains.compose.web.attributes.InputType
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
                BackButton { navigator.replaceAll(OrdersScreen()) }
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("New Order") }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    Card(classes = "p-6") {
                        Div(attrs = { classes("space-y-5") }) {
                            InputField("Vehicle (Make, Model, Year)", state.carModelYear, "Toyota Camry 2022", required = true) {
                                model.onEvent(NewOrderEvent.SetCar(it))
                            }
                            TextArea("Description", state.comment, "Describe what you need…", 4) {
                                model.onEvent(NewOrderEvent.SetComment(it))
                            }

                            if (state.services.isNotEmpty()) {
                                Div {
                                    P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) { Text("Services Needed") }
                                    Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                                        state.services.forEach { svc ->
                                            val sel = svc.id in state.selectedServices
                                            Span(attrs = {
                                                style { property("cursor", "pointer") }
                                                onClick { model.onEvent(NewOrderEvent.ToggleService(svc.id)) }
                                            }) {
                                                Badge(svc.name, variant = if (sel) BadgeVariant.Brand else BadgeVariant.Alternative, pill = true)
                                            }
                                        }
                                    }
                                }
                            }

                            Div {
                                P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) { Text("Spare Parts Needed") }
                                Div(attrs = { classes("space-y-2") }) {
                                    state.spareParts.forEachIndexed { i, part ->
                                        Div(attrs = { classes("flex", "gap-2", "items-center") }) {
                                            Input(type = InputType.Text, attrs = {
                                                classes(
                                                    "flex-1", "px-4", "py-2.5", "border", "border-default-medium", "rounded-neu-base",
                                                    "bg-surface", "shadow-neu-inset", "text-sm", "text-heading",
                                                    "focus:outline-none", "focus:ring-1", "focus:ring-brand", "focus:border-brand"
                                                )
                                                attr("placeholder", "e.g. Brake pad")
                                                attr("value", part)
                                                onInput { model.onEvent(NewOrderEvent.SetSparePart(i, it.value)) }
                                            })
                                            if (state.spareParts.size > 1) {
                                                Button(attrs = {
                                                    attr("type", "button")
                                                    classes("px-3", "py-2", "text-fg-danger", "hover:opacity-70", "text-sm", "transition-opacity")
                                                    style { property("background", "none"); property("border", "none"); property("cursor", "pointer") }
                                                    onClick { model.onEvent(NewOrderEvent.RemoveSparePart(i)) }
                                                }) { Text("✕") }
                                            }
                                        }
                                    }
                                    Button(attrs = {
                                        attr("type", "button")
                                        classes("text-sm", "text-fg-brand", "hover:underline")
                                        style { property("background", "none"); property("border", "none"); property("cursor", "pointer") }
                                        onClick { model.onEvent(NewOrderEvent.AddSparePart) }
                                    }) { Text("+ Add spare part") }
                                }
                            }

                            PrimaryButton("Submit Order", loading = state.submitting, fullWidth = true) {
                                model.onEvent(NewOrderEvent.Submit)
                            }
                        }
                    }
                }
            }
        }
    }
}
