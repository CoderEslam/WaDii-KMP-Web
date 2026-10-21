package com.wadii.pages.provider.respond

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.domain.model.order.OrderModel
import com.wadii.pages.provider.orders.ProviderOrdersScreen
import com.wadii.pages.shared.chat.ChatScreen
import com.wadii.screens.orders.OrderProgress
import com.wadii.screens.orders.OrderStage
import com.wadii.screens.orders.StageBadge
import com.wadii.screens.orders.canStart
import com.wadii.screens.orders.chatUnlocked
import com.wadii.screens.orders.isAccepted
import com.wadii.screens.orders.isDeclined
import com.wadii.screens.orders.isOpen
import com.wadii.screens.orders.isPending
import com.wadii.screens.orders.isWithdrawn
import com.wadii.screens.orders.stage
import com.wadii.state.AppState
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.BackButton
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.DangerButton
import com.wadii.ui.GhostButton
import com.wadii.ui.LoadingScreen
import com.wadii.ui.PrimaryButton
import com.wadii.ui.SecondaryButton
import com.wadii.ui.SuccessButton
import com.wadii.ui.TextArea
import com.wadii.utils.formatPrice
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

                state.order.id != 0 -> {
                    val o = state.order
                    val stage = o.stage()
                    val myProviderId = AppState.user?.provider?.id
                    val mine = o.responses.firstOrNull { it.provider.id == myProviderId } ?: o.responses.firstOrNull()

                    if (state.error.isNotBlank()) Alert(variant = AlertVariant.Danger, body = state.error)

                    OrderSummary(o, stage)

                    when {
                        // The customer picked this provider: the job is theirs.
                        mine != null && mine.isAccepted -> WonCard(o, state, model, navigator)

                        // A pending answer on an open order can still be changed or withdrawn.
                        mine != null && mine.isPending && o.isOpen ->
                            if (state.editing) {
                                ResponseForm(o, state, model, orderId, title = "Edit your response", submitLabel = "Save changes", onCancel = { model.onEvent(RespondToOrderEvent.CancelEdit) })
                            } else {
                                MyResponseCard(mine, state, model)
                            }

                        // Answered, but the customer chose someone else, declined it, or it was withdrawn.
                        mine != null -> Alert(
                            variant = if (mine.isDeclined || mine.isWithdrawn) AlertVariant.Warning else AlertVariant.Brand,
                            body = when {
                                mine.isDeclined -> "The customer didn't choose your response. You can't answer this order again."
                                mine.isWithdrawn -> "You withdrew your response, so you can't answer this order again."
                                else -> "Your response is no longer active because the order is ${stage.label.lowercase()}."
                            }
                        )

                        // Not answered yet and still open: the form.
                        o.isOpen -> ResponseForm(o, state, model, orderId, title = "Submit Response", submitLabel = "Submit Response", onCancel = null)

                        else -> Alert(
                            variant = AlertVariant.Warning,
                            body = if (stage == OrderStage.Cancelled) "The customer cancelled this order." else "This order is no longer open for responses."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderSummary(o: OrderModel, stage: OrderStage) {
    Card(classes = "p-6") {
        Div(attrs = { classes("flex", "items-start", "justify-between", "gap-2", "mb-4") }) {
            H1(attrs = { classes("text-xl", "font-semibold", "text-heading") }) { Text("Order #${o.id}") }
            StageBadge(stage)
        }
        Div(attrs = { classes("mb-4") }) { OrderProgress(stage) }
        Div(attrs = { classes("space-y-2", "text-sm") }) {
            P {
                Span(attrs = { classes("text-body-subtle") }) { Text("Vehicle: ") }
                Span(attrs = { classes("font-medium", "text-heading") }) { Text(o.carModelYear) }
            }
            if (o.date.isNotBlank()) {
                P {
                    Span(attrs = { classes("text-body-subtle") }) { Text("Needed by: ") }
                    Span(attrs = { classes("text-body") }) { Text(o.date.take(10)) }
                }
            }
            if (o.comment.isNotBlank()) {
                P {
                    Span(attrs = { classes("text-body-subtle") }) { Text("Description: ") }
                    Span(attrs = { classes("text-body") }) { Text(o.comment) }
                }
            }
            o.services.takeIf { it.isNotEmpty() }?.let { svcs ->
                Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                    svcs.forEach { s -> Badge(s.name, variant = BadgeVariant.Brand, pill = true) }
                }
            }
            o.spareParts.takeIf { it.isNotEmpty() }?.let { parts ->
                Div(attrs = { classes("mt-2") }) {
                    P(attrs = { classes("text-body-subtle", "mb-1") }) { Text("Spare parts:") }
                    parts.forEach { sp -> P(attrs = { classes("text-body") }) { Text("• ${sp.sparePartName}") } }
                }
            }
        }
    }
}

/** The pending answer, with edit and withdraw. */
@Composable
private fun MyResponseCard(mine: OrderModel.OrderResponse, state: RespondToOrderState, model: RespondToOrderViewModel) {
    Card(classes = "p-6") {
        Div(attrs = { classes("flex", "items-center", "justify-between", "mb-3") }) {
            H2(attrs = { classes("font-semibold", "text-heading") }) { Text("Your response") }
            Badge("Waiting for the customer", variant = BadgeVariant.Warning, pill = true, icon = "⏳")
        }
        P(attrs = { classes("text-sm", "text-body") }) { Text(mine.comment) }
        if (mine.sparePartsPrices.isNotEmpty()) {
            Div(attrs = { classes("mt-4", "space-y-1") }) {
                mine.sparePartsPrices.forEach { price ->
                    Div(attrs = { classes("flex", "justify-between", "text-sm") }) {
                        Span(attrs = { classes("text-body") }) { Text(price.sparePart.sparePartName) }
                        Span(attrs = { classes("font-medium", "text-heading") }) { Text(formatPrice(price.price)) }
                    }
                }
                Div(attrs = { classes("flex", "justify-between", "text-sm", "pt-2", "border-t", "border-default") }) {
                    Span(attrs = { classes("font-semibold", "text-heading") }) { Text("Total") }
                    Span(attrs = { classes("font-semibold", "text-heading") }) { Text(formatPrice(mine.totalPrice)) }
                }
            }
        }
        Div(attrs = { classes("flex", "gap-2", "mt-5") }) {
            SecondaryButton("✏️ Edit", disabled = state.busy) { model.onEvent(RespondToOrderEvent.StartEdit) }
            DangerButton("Withdraw", loading = state.busy, disabled = state.busy) { model.onEvent(RespondToOrderEvent.Withdraw) }
        }
        P(attrs = { classes("text-xs", "text-body-subtle", "mt-3") }) {
            Text("Withdrawing is final: you can't answer this order again afterwards.")
        }
    }
}

/** The customer chose this provider: chat with them and run the job. */
@Composable
private fun WonCard(
    o: OrderModel,
    state: RespondToOrderState,
    model: RespondToOrderViewModel,
    navigator: cafe.adriel.voyager.navigator.Navigator
) {
    val stage = o.stage()
    Card(classes = "p-6") {
        Div(attrs = { classes("space-y-3") }) {
            H2(attrs = { classes("font-semibold", "text-heading") }) {
                Text(
                    when (stage) {
                        OrderStage.Accepted -> "🎉 You got the job!"
                        OrderStage.InProgress -> "🔧 Job in progress"
                        OrderStage.Completed -> "🏁 Job completed"
                        else -> "Your response"
                    }
                )
            }
            P(attrs = { classes("text-sm", "text-body-subtle") }) {
                Text(
                    when (stage) {
                        OrderStage.Accepted -> "The customer accepted your response. Agree the details in chat, then start the job."
                        OrderStage.InProgress -> "The customer will mark the order completed when the job is done."
                        OrderStage.Completed -> "The customer closed this order and can now rate your work."
                        else -> "This order is no longer active."
                    }
                )
            }
            Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                if (o.chatUnlocked) {
                    SecondaryButton("💬 Chat with ${o.user.firstName.ifBlank { "customer" }}") { navigator.push(ChatScreen(o.customerChatContact())) }
                }
                if (o.canStart) {
                    SuccessButton("Start the job", loading = state.busy, disabled = state.busy, icon = "▶") {
                        model.onEvent(RespondToOrderEvent.StartJob)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResponseForm(
    o: OrderModel,
    state: RespondToOrderState,
    model: RespondToOrderViewModel,
    orderId: Int,
    title: String,
    submitLabel: String,
    onCancel: (() -> Unit)?
) {
    Card(classes = "p-6") {
        H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text(title) }
        Div(attrs = { classes("space-y-5") }) {
            TextArea(
                "Your Comment",
                state.comment,
                "Describe your parts availability, pricing, timeline…",
                4,
                required = true,
                error = state.fieldErrors["comment"]
            ) { model.onEvent(RespondToOrderEvent.SetComment(it)) }
            if (state.prices.isNotEmpty()) {
                Div {
                    P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) { Text("Spare Parts Pricing") }
                    state.fieldErrors.entries.firstOrNull { it.key.startsWith("price") }?.let { e ->
                        P(attrs = { classes("field-error", "mb-2"); attr("role", "alert") }) { Text(e.value) }
                    }
                    Div(attrs = { classes("space-y-2") }) {
                        o.spareParts.forEachIndexed { i, sp ->
                            Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                                Span(attrs = { classes("flex-1", "text-sm", "text-body") }) { Text(sp.sparePartName) }
                                Div(attrs = { classes("relative"); style { property("width", "150px") } }) {
                                    Span(attrs = {
                                        classes("absolute", "left-3", "top-1/2", "-translate-y-1/2", "text-body-subtle", "text-sm", "pointer-events-none")
                                    }) { Text("EGP") }
                                    Input(type = InputType.Text, attrs = {
                                        classes(
                                            "w-full", "pl-12", "pr-3", "py-2", "border", "border-default-medium", "rounded-neu-base",
                                            "bg-surface", "shadow-neu-inset", "text-sm", "text-heading",
                                            "focus:outline-none", "focus:ring-1", "focus:ring-brand", "focus:border-brand"
                                        )
                                        if (state.fieldErrors["price$i"] != null) attr("aria-invalid", "true")
                                        attr("placeholder", "0.00")
                                        attr("type", "number"); attr("min", "0"); attr("step", "0.01")
                                        attr("value", state.prices.getOrNull(i)?.second ?: "")
                                        onInput { model.onEvent(RespondToOrderEvent.SetPrice(i, it.value)) }
                                    })
                                }
                            }
                        }
                    }
                }
            }
            Div(attrs = { classes("flex", "gap-2") }) {
                Div(attrs = { classes("flex-1") }) {
                    PrimaryButton(submitLabel, loading = state.submitting, fullWidth = true) {
                        model.onEvent(RespondToOrderEvent.Submit(orderId))
                    }
                }
                onCancel?.let { SecondaryButton("Cancel", disabled = state.submitting) { it() } }
            }
        }
    }
}
