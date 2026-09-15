package com.wadii.pages.provider.respond

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.core.isNotNullOrEmptyString
import com.wadii.pages.provider.orders.ProviderOrdersScreen
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.BackButton
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.LoadingScreen
import com.wadii.ui.PrimaryButton
import com.wadii.ui.TextArea
import org.jetbrains.compose.web.attributes.InputType
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
            BackButton("Back to orders") { navigator.pop() }

            when {
                state.isLoading -> LoadingScreen()
//                state.error.isNotNullOrEmptyString() -> Alert(
//                    variant = AlertVariant.Danger,
//                    body = state.error
//                )

                state.order.id != 0 -> {
                    val o = state.order
                    Card(classes = "p-6") {
                        H1(attrs = {
                            classes(
                                "text-xl",
                                "font-semibold",
                                "text-heading",
                                "mb-4"
                            )
                        }) { Text("Order #${o.id}") }
                        Div(attrs = { classes("space-y-2", "text-sm") }) {
                            P {
                                Span(attrs = { classes("text-body-subtle") }) { Text("Vehicle: ") }; Span(
                                attrs = {
                                    classes(
                                        "font-medium",
                                        "text-heading"
                                    )
                                }) { Text(o.carModelYear) }
                            }
                            P {
                                Span(attrs = { classes("text-body-subtle") }) { Text("Description: ") }; Span(
                                attrs = { classes("text-body") }) { Text(o.comment) }
                            }
                            o.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                    svcs.forEach { s ->
                                        Badge(
                                            s.name,
                                            variant = BadgeVariant.Brand,
                                            pill = true
                                        )
                                    }
                                }
                            }
                            o.spareParts?.takeIf { it.isNotEmpty() }?.let { parts ->
                                Div(attrs = { classes("mt-2") }) {
                                    P(attrs = {
                                        classes(
                                            "text-body-subtle",
                                            "mb-1"
                                        )
                                    }) { Text("Spare parts:") }
                                    parts.forEach { sp ->
                                        P(attrs = { classes("text-body") }) {
                                            Text(
                                                "• ${sp.sparePartName}"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Card(classes = "p-6") {
                        H2(attrs = {
                            classes(
                                "font-semibold",
                                "text-heading",
                                "mb-4"
                            )
                        }) { Text("Submit Response") }
                        Div(attrs = { classes("space-y-5") }) {
                            TextArea(
                                "Your Comment",
                                state.comment,
                                "Describe your parts availability, pricing, timeline…",
                                4
                            ) {
                                model.onEvent(RespondToOrderEvent.SetComment(it))
                            }
                            if (state.prices.isNotEmpty() && o.spareParts != null) {
                                Div {
                                    P(attrs = {
                                        classes(
                                            "text-sm",
                                            "font-medium",
                                            "text-heading",
                                            "mb-2"
                                        )
                                    }) { Text("Spare Parts Pricing") }
                                    Div(attrs = { classes("space-y-2") }) {
                                        o.spareParts!!.forEachIndexed { i, sp ->
                                            Div(attrs = {
                                                classes(
                                                    "flex",
                                                    "items-center",
                                                    "gap-3"
                                                )
                                            }) {
                                                Span(attrs = {
                                                    classes(
                                                        "flex-1",
                                                        "text-sm",
                                                        "text-body"
                                                    )
                                                }) { Text(sp.sparePartName) }
                                                Div(attrs = {
                                                    classes("relative"); style {
                                                    property(
                                                        "width",
                                                        "128px"
                                                    )
                                                }
                                                }) {
                                                    Span(attrs = {
                                                        classes(
                                                            "absolute",
                                                            "left-3",
                                                            "top-1/2",
                                                            "-translate-y-1/2",
                                                            "text-body-subtle",
                                                            "text-sm",
                                                            "pointer-events-none"
                                                        )
                                                    }) { Text("$") }
                                                    Input(type = InputType.Text, attrs = {
                                                        classes(
                                                            "w-full",
                                                            "pl-7",
                                                            "pr-3",
                                                            "py-2",
                                                            "border",
                                                            "border-default-medium",
                                                            "rounded-neu-base",
                                                            "bg-surface",
                                                            "shadow-neu-inset",
                                                            "text-sm",
                                                            "text-heading",
                                                            "focus:outline-none",
                                                            "focus:ring-1",
                                                            "focus:ring-brand",
                                                            "focus:border-brand"
                                                        )
                                                        attr("placeholder", "0.00"); attr(
                                                        "type",
                                                        "number"
                                                    ); attr("min", "0"); attr("step", "0.01")
                                                        attr(
                                                            "value",
                                                            state.prices.getOrNull(i)?.second ?: ""
                                                        )
                                                        onInput {
                                                            model.onEvent(
                                                                RespondToOrderEvent.SetPrice(
                                                                    i,
                                                                    it.value
                                                                )
                                                            )
                                                        }
                                                    })
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            PrimaryButton(
                                "Submit Response",
                                loading = state.submitting,
                                fullWidth = true
                            ) {
                                model.onEvent(RespondToOrderEvent.Submit(orderId))
                            }
                        }
                    }
                }
            }
        }
    }
}
