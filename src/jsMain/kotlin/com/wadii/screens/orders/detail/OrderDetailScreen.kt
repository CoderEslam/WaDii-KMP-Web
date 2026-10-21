package com.wadii.screens.orders.detail

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.domain.model.order.OrderModel
import com.wadii.pages.shared.chat.ChatScreen
import com.wadii.screens.orders.OrderProgress
import com.wadii.screens.orders.OrderStage
import com.wadii.screens.orders.StageBadge
import com.wadii.screens.orders.acceptedResponse
import com.wadii.screens.orders.canComplete
import com.wadii.screens.orders.chatUnlocked
import com.wadii.screens.orders.isAccepted
import com.wadii.screens.orders.isDeclined
import com.wadii.screens.orders.isOpen
import com.wadii.screens.orders.isPending
import com.wadii.screens.orders.isWithdrawn
import com.wadii.screens.orders.stage
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.BackButton
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.DangerButton
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.LoadingScreen
import com.wadii.ui.PrimaryButton
import com.wadii.ui.StarPicker
import com.wadii.ui.SuccessButton
import com.wadii.ui.TextArea
import com.wadii.utils.formatPrice
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
                state.error != null && state.order == null -> Alert(variant = AlertVariant.Danger, body = state.error!!)

                state.order != null -> {
                    val o = state.order!!
                    val stage = o.stage()
                    Card(classes = "p-6") {
                        Div(attrs = { classes("space-y-4") }) {
                            Div(attrs = { classes("flex", "justify-between", "items-start") }) {
                                H1(attrs = { classes("text-xl", "font-semibold", "text-heading") }) { Text("Order #${o.id}") }
                                P(attrs = { classes("text-xs", "text-body-subtle") }) { Text(o.date.take(10)) }
                            }
                            Div(attrs = { classes("flex", "flex-wrap", "items-center", "justify-between", "gap-2") }) {
                                StageBadge(stage)
                                OrderProgress(stage)
                            }
                            OrderInfoRow("Vehicle", o.carModelYear)
                            if (o.comment.isNotBlank()) OrderInfoRow("Description", o.comment)
                            o.services.takeIf { it.isNotEmpty() }?.let { svcs ->
                                Div {
                                    P(attrs = { classes("text-xs", "font-medium", "text-body-subtle", "uppercase", "tracking-wide", "mb-1") }) { Text("Services") }
                                    Div(attrs = { classes("flex", "flex-wrap", "gap-1") }) {
                                        svcs.forEach { s -> Badge(s.name, variant = BadgeVariant.Brand, pill = true) }
                                    }
                                }
                            }
                            o.spareParts.takeIf { it.isNotEmpty() }?.let { parts ->
                                Div {
                                    P(attrs = { classes("text-xs", "font-medium", "text-body-subtle", "uppercase", "tracking-wide", "mb-1") }) { Text("Spare Parts") }
                                    parts.forEach { sp ->
                                        P(attrs = { classes("text-sm", "text-body") }) { Text("• ${sp.sparePartName}") }
                                    }
                                }
                            }
                        }
                    }

                    state.error?.let { Alert(variant = AlertVariant.Danger, body = it) }

                    // ── What happens next ────────────────────────────────────────
                    NextStepCard(o, stage, model, state)

                    H2(attrs = { classes("font-semibold", "text-heading") }) { Text("Responses (${o.responses.size})") }
                    if (o.responses.isEmpty()) {
                        EmptyState(
                            "💬",
                            if (stage == OrderStage.Cancelled) "This order was cancelled" else "No proposals yet",
                            if (stage == OrderStage.Cancelled) "Sellers can no longer reply to it." else "Sellers are looking at your request. We'll notify you when one replies."
                        )
                    } else {
                        Div(attrs = { classes("space-y-3", "stagger") }) {
                            // The winner first, then the rest in the order they came in.
                            o.responses.sortedByDescending { it.isAccepted }.forEach { resp ->
                                ResponseCard(
                                    resp = resp,
                                    order = o,
                                    model = model,
                                    deciding = state.decidingResponseId == resp.id.toLong(),
                                    canDecide = o.isOpen && resp.isPending && state.decidingResponseId == null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** The one thing the customer can do now: wait, finish the job, or rate it. */
@Composable
private fun NextStepCard(o: OrderModel, stage: OrderStage, model: OrderDetailViewModel, state: OrderDetailState) {
    when {
        stage == OrderStage.Waiting -> Alert(
            variant = AlertVariant.Brand,
            body = "Your request has been sent to matching sellers. You can still edit it until the first proposal arrives."
        )

        stage == OrderStage.HasProposals -> Alert(
            variant = AlertVariant.Brand,
            body = "Compare the proposals below and accept the one you like. The others are declined automatically."
        )

        o.canComplete -> Card(classes = "p-5") {
            Div(attrs = { classes("space-y-3") }) {
                H2(attrs = { classes("font-semibold", "text-heading") }) {
                    Text(if (stage == OrderStage.InProgress) "🔧 The seller is working on it" else "🤝 You have a deal")
                }
                P(attrs = { classes("text-sm", "text-body-subtle") }) {
                    Text("Chat with the seller to agree the details. Mark the order as completed once the job is done, then you can rate the seller.")
                }
                SuccessButton("Mark as completed", loading = state.completing, disabled = state.completing, icon = "✓", fullWidth = true) {
                    model.onEvent(OrderDetailEvent.Complete)
                }
            }
        }

        stage == OrderStage.Completed -> Card(classes = "p-6") {
            if (state.rated) {
                Div(attrs = { classes("text-center", "space-y-1") }) {
                    P(attrs = { classes("text-2xl") }) { Text("🎉") }
                    P(attrs = { classes("font-semibold", "text-heading") }) { Text("Thanks for your rating!") }
                    P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("It helps other buyers choose well.") }
                }
            } else {
                val seller = o.acceptedResponse?.provider?.name?.ifBlank { null } ?: "the seller"
                H2(attrs = { classes("font-semibold", "text-heading", "mb-1") }) { Text("⭐ Rate $seller") }
                P(attrs = { classes("text-sm", "text-body-subtle", "mb-4") }) { Text("You can rate each completed order once.") }
                StarPicker(state.stars, state.ratingErrors["rate"]) { model.onEvent(OrderDetailEvent.SetRating(it)) }
                Div(attrs = { classes("space-y-4", "mt-3") }) {
                    TextArea(
                        "Your review (optional)", state.ratingComment, "What was it like to work with this seller?", 3,
                        error = state.ratingErrors["comment"]
                    ) { model.onEvent(OrderDetailEvent.SetRatingComment(it)) }
                    PrimaryButton("Submit rating", loading = state.ratingSubmitting, fullWidth = true) {
                        model.onEvent(OrderDetailEvent.SubmitRating)
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
private fun ResponseCard(
    resp: OrderModel.OrderResponse,
    order: OrderModel,
    model: OrderDetailViewModel,
    deciding: Boolean,
    canDecide: Boolean
) {
    val navigator = LocalNavigator.currentOrThrow
    var expanded by remember(resp.id) { mutableStateOf(false) }
    val provider = resp.provider
    val user = provider.user
    val accepted = resp.isAccepted

    Card(classes = "p-5") {
        Div(attrs = {
            classes("flex", "items-start", "justify-between", "gap-3")
            style { property("cursor", "pointer") }
            onClick { expanded = !expanded }
        }) {
            Div(attrs = { classes("flex-1", "min-w-0") }) {
                P(attrs = { classes("font-medium", "text-heading", "mb-1") }) { Text("👤 ${provider.name.ifBlank { "Seller" }}") }
                P(attrs = { classes("text-sm", "text-body") }) { Text(resp.comment) }
                if (resp.totalPrice > 0) {
                    P(attrs = { classes("text-sm", "font-semibold", "text-heading", "mt-2") }) { Text("Total ${formatPrice(resp.totalPrice)}") }
                }
            }
            Div(attrs = { classes("flex", "items-center", "gap-2", "flex-shrink-0") }) {
                when {
                    accepted -> Badge("Accepted", variant = BadgeVariant.Success, pill = true, icon = "🤝")
                    resp.isDeclined -> Badge("Declined", variant = BadgeVariant.Danger, pill = true)
                    resp.isWithdrawn -> Badge("Withdrawn", variant = BadgeVariant.Alternative, pill = true)
                    else -> Badge("New proposal", variant = BadgeVariant.Brand, pill = true, icon = "💌")
                }
                Span(attrs = { classes("text-body-subtle", "text-sm") }) { Text(if (expanded) "▲" else "▼") }
            }
        }
        // Accept or decline lives right on the proposal. Chat opens only with the seller you accepted:
        // the server refuses messages between users without an accepted order.
        Div(attrs = { classes("flex", "flex-wrap", "items-center", "gap-2", "mt-4") }) {
            if (canDecide) {
                SuccessButton("Accept", loading = deciding, disabled = deciding, icon = "✓") {
                    model.onEvent(OrderDetailEvent.AcceptResponse(resp.id.toLong(), order.id))
                }
                DangerButton("Decline", disabled = deciding) {
                    model.onEvent(OrderDetailEvent.DeclineResponse(resp.id.toLong(), order.id))
                }
            }
            if (accepted && order.chatUnlocked) {
                GhostButton("Chat with seller", icon = "💬") { navigator.push(ChatScreen(provider.toChatContent())) }
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
                    // Contact details are only for the seller you picked; before that, chat is closed anyway.
                    if (accepted) {
                        if (user.email.isNotBlank()) P(attrs = { classes("text-sm", "text-body") }) { Text("✉️ ${user.email}") }
                        if (user.phone.isNotBlank()) P(attrs = { classes("text-sm", "text-body") }) { Text("📞 ${user.phone}") }
                    }
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
                                Span(attrs = { classes("font-medium", "text-heading") }) { Text(formatPrice(price.price)) }
                            }
                        }
                    }
                }
            }
        }
    }
}
