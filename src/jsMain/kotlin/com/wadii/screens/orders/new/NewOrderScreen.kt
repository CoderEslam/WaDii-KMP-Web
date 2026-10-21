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
import com.wadii.ui.SelectField
import com.wadii.ui.TextArea
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf

/**
 * [providerId] > 0 makes this a direct request to that seller (see ProviderDetailScreen); [offeredServiceIds] limits the
 * service choices to what they offer, because the server rejects a direct order for anything else.
 */
class NewOrderScreen(
    private val providerId: Long = 0,
    private val providerName: String = "",
    private val offeredServiceIds: List<Long> = emptyList()
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<NewOrderViewModel> { parametersOf(providerId, offeredServiceIds) }
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

            if (providerId > 0) {
                Alert(
                    variant = AlertVariant.Brand,
                    body = "This request goes only to ${providerName.ifBlank { "this seller" }}. Leave it open to every matching seller by ordering from My Orders instead."
                )
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                else -> {
                    Card(classes = "p-6") {
                        Div(attrs = { classes("space-y-5") }) {
                            // Make comes from the admin-managed list (GET /car-types/show-all); model + year is typed.
                            val carError = state.fieldErrors["car"]
                            if (state.carTypes.isNotEmpty()) {
                                Div(attrs = { classes("grid", "grid-cols-1", "sm:grid-cols-2", "gap-4") }) {
                                    SelectField(
                                        label = "Car make",
                                        options = listOf("") + state.carTypes.map { it.name },
                                        selected = state.carMake,
                                        optionLabel = { it.ifEmpty { "Select make…" } },
                                        required = true,
                                        error = carError?.takeIf { state.carMake.isBlank() }
                                    ) { model.onEvent(NewOrderEvent.SetCarMake(it)) }
                                    InputField(
                                        "Model & year", state.carDetails, "Camry 2022",
                                        required = true, error = carError?.takeIf { state.carMake.isNotBlank() }
                                    ) { model.onEvent(NewOrderEvent.SetCar(it)) }
                                }
                            } else {
                                // Car list unavailable (still loading or failed): fall back to free text so ordering still works.
                                InputField(
                                    "Vehicle (Make, Model, Year)", state.carDetails, "Toyota Camry 2022",
                                    required = true, error = carError
                                ) { model.onEvent(NewOrderEvent.SetCar(it)) }
                            }
                            InputField(
                                "Needed by", state.date, type = "date", required = true,
                                error = state.fieldErrors["date"], hint = "The day you would like the job done"
                            ) { model.onEvent(NewOrderEvent.SetDate(it)) }
                            TextArea("Description", state.comment, "Describe what you need…", 4) {
                                model.onEvent(NewOrderEvent.SetComment(it))
                            }

                            if (state.services.isNotEmpty()) {
                                Div {
                                    P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) {
                                        Text("Services Needed")
                                        Span(attrs = { classes("text-fg-danger", "ml-0.5") }) { Text("*") }
                                    }
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
                                    state.fieldErrors["services"]?.let { P(attrs = { classes("field-error", "mt-2"); attr("role", "alert") }) { Text(it) } }
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
                                    val unused = state.suggestions.filter { s -> state.spareParts.none { it.equals(s.name, ignoreCase = true) } }
                                    if (unused.isNotEmpty()) {
                                        P(attrs = { classes("text-xs", "text-body-subtle", "mt-2") }) { Text("Popular parts, tap to add:") }
                                        Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                                            unused.take(12).forEach { s ->
                                                Span(attrs = {
                                                    style { property("cursor", "pointer") }
                                                    onClick { model.onEvent(NewOrderEvent.UseSuggestion(s.name)) }
                                                }) { Badge("+ ${s.name}", variant = BadgeVariant.Alternative, pill = true) }
                                            }
                                        }
                                    }
                                    state.fieldErrors["parts"]?.let { P(attrs = { classes("field-error"); attr("role", "alert") }) { Text(it) } }
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
