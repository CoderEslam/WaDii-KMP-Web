package com.wadii.screens.orders.list

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.domain.model.order.CancelReason
import com.wadii.screens.orders.detail.OrderDetailScreen
import com.wadii.screens.orders.edit.EditOrderScreen
import com.wadii.screens.orders.new.NewOrderScreen
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.DangerButton
import com.wadii.ui.EmptyState
import com.wadii.ui.LoadingScreen
import com.wadii.ui.Modal
import com.wadii.ui.ModalVariant
import com.wadii.ui.PageHeader
import com.wadii.ui.PrimaryButton
import com.wadii.ui.RadioGroup
import com.wadii.ui.SecondaryButton
import com.wadii.ui.Spinner
import org.jetbrains.compose.web.dom.*

class OrdersScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<OrdersViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            PageHeader("My Orders", "+ New Order") { navigator.push(NewOrderScreen()) }

            if (state.isLoading) {
                LoadingScreen()
            } else if (state.error != null) {
                Alert(variant = AlertVariant.Danger, body = state.error!!)
            } else {
                val orders = state.orders
                if (orders.isEmpty()) {
                    Div(attrs = { classes("flex", "flex-col", "items-center", "gap-4") }) {
                        EmptyState("📦", "You haven't placed any orders yet.")
                        PrimaryButton("Create your first order") { navigator.push(NewOrderScreen()) }
                    }
                } else {
                    Div(attrs = { classes("space-y-3") }) {
                        orders.forEach { order ->
                            Div(attrs = {
                                style { property("cursor", "pointer") }
                                onClick { navigator.push(OrderDetailScreen(order.id)) }
                            }) {
                                Card(classes = "p-5 hover:shadow-neu-md active:shadow-neu-inset transition-all") {
                                    Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                                        Div {
                                            P(attrs = { classes("font-semibold", "text-heading") }) { Text("Order #${order.id} — ${order.carModelYear}") }
                                            P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) { Text(order.comment.take(80)) }
                                            order.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                                Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                                    svcs.forEach { s -> Badge(s.name, variant = BadgeVariant.Alternative, pill = true) }
                                                }
                                            }
                                        }
                                        Div(attrs = { classes("text-right") }) {
                                            P(attrs = { classes("text-xs", "text-body-subtle") }) { Text(order.date.take(10)) }
                                            val latestState = order.responses?.firstOrNull()?.state
                                            if (latestState != null) {
                                                val variant = when (latestState) {
                                                    "ACCEPT" -> BadgeVariant.Success
                                                    "CANCEL" -> BadgeVariant.Danger
                                                    else -> BadgeVariant.Warning
                                                }
                                                val label = when (latestState) { "ACCEPT" -> "Accepted"; "CANCEL" -> "Declined"; else -> "Pending" }
                                                Div(attrs = { classes("mt-1") }) { Badge(label, variant = variant, pill = true) }
                                            }
                                        }
                                    }
                                    Div(attrs = {
                                        classes("flex", "items-center", "gap-2", "mt-4", "pt-4", "border-t", "border-default")
                                        onClick { it.stopPropagation() }
                                    }) {
                                        SecondaryButton("✏️ Edit") { navigator.push(EditOrderScreen(order.id)) }
                                        DangerButton("✕ Cancel") { model.onEvent(OrdersEvent.OpenCancelDialog(order)) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Modal(
            open = state.cancelingOrder != null,
            title = "Cancel order #${state.cancelingOrder?.id ?: 0}?",
            onDismiss = { model.onEvent(OrdersEvent.DismissCancelDialog) },
            variant = ModalVariant.Form,
            footer = {
                SecondaryButton("Keep Order", disabled = state.isCancelling) {
                    model.onEvent(OrdersEvent.DismissCancelDialog)
                }
                DangerButton(
                    "Confirm Cancel",
                    loading = state.isCancelling,
                    disabled = state.selectedReasonId == null,
                    onClick = { model.onEvent(OrdersEvent.ConfirmCancel) }
                )
            }
        ) {
            when {
                state.loadingCancelReasons -> Div(attrs = { classes("flex", "justify-center", "py-4") }) { Spinner() }
                state.cancelReasons.isEmpty() -> P(attrs = { classes("text-sm", "text-body-subtle") }) {
                    Text("No cancellation reasons available.")
                }
                else -> {
                    P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-3") }) { Text("Select a reason for cancellation") }
                    RadioGroup(
                        name = "cancel-reason",
                        options = state.cancelReasons,
                        selected = state.cancelReasons.find { it.id == state.selectedReasonId } ?: CancelReason(),
                        itemLabel = { it.reason }
                    ) { model.onEvent(OrdersEvent.SelectCancelReason(it.id)) }
                }
            }
        }
    }
}
