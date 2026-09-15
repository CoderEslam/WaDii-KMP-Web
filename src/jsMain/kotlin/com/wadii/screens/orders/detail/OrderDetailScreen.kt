package com.wadii.screens.orders.detail

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.domain.model.order.OrderModel
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.BackButton
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.LoadingScreen
import org.jetbrains.compose.web.css.*
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
                    if (o.responses.isEmpty()) {
                        EmptyState("💬", "No responses yet. Sellers will respond soon.")
                    } else {
                        Div(attrs = { classes("space-y-3") }) {
                            o.responses.forEach { resp ->
                                ResponseCard(resp)
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

@Composable
private fun ResponseCard(resp: OrderModel.OrderResponse) {
    var expanded by remember(resp.id) { mutableStateOf(false) }
    val provider = resp.provider
    val user = provider.user

    Card(classes = "p-5") {
        Div(attrs = {
            classes("flex", "items-start", "justify-between", "gap-3")
            style { property("cursor", "pointer") }
            onClick { expanded = !expanded }
        }) {
            Div(attrs = { classes("flex-1", "min-w-0") }) {
                P(attrs = { classes("font-medium", "text-heading", "mb-1") }) { Text("👤 ${provider.name.ifBlank { "Seller" }}") }
                P(attrs = { classes("text-sm", "text-body") }) { Text(resp.comment) }
            }
            Div(attrs = { classes("flex", "items-center", "gap-2", "flex-shrink-0") }) {
                if (resp.state.isNotBlank()) Badge(resp.state, variant = BadgeVariant.Gray, pill = true)
                Span(attrs = { classes("text-body-subtle", "text-sm") }) { Text(if (expanded) "▲" else "▼") }
            }
        }
        if (expanded) {
            Div(attrs = { classes("mt-4", "pt-4", "border-t", "border-default", "space-y-4") }) {
                Div(attrs = { classes("space-y-1") }) {
                    P(attrs = { classes("text-xs", "font-medium", "text-body-subtle", "uppercase", "tracking-wide") }) { Text("Provider") }
                    Div(attrs = { classes("flex", "flex-wrap", "gap-3", "text-sm", "text-body") }) {
                        Span { Text("⭐ ${provider.rate}") }
                        Span { Text("👥 ${provider.followersCount} followers") }
                    }
                    if (user.email.isNotBlank()) P(attrs = { classes("text-sm", "text-body") }) { Text("✉️ ${user.email}") }
                    if (user.phone.isNotBlank()) P(attrs = { classes("text-sm", "text-body") }) { Text("📞 ${user.phone}") }
                    user.city?.let { city ->
                        P(attrs = { classes("text-sm", "text-body") }) { Text("📍 ${city.name}, ${city.province.name}, ${city.province.country.name}") }
                    }
                }
                resp.sparePartsPrices.takeIf { it.isNotEmpty() }?.let { prices ->
                    Div(attrs = { classes("space-y-1") }) {
                        P(attrs = { classes("text-xs", "font-medium", "text-body-subtle", "uppercase", "tracking-wide") }) { Text("Pricing") }
                        prices.forEach { price ->
                            Div(attrs = { classes("flex", "justify-between", "text-sm") }) {
                                Span(attrs = { classes("text-body") }) { Text(price.sparePart.sparePartName) }
                                Span(attrs = { classes("font-medium", "text-heading") }) { Text("$${price.price}") }
                            }
                        }
                    }
                }
            }
        }
    }
}
