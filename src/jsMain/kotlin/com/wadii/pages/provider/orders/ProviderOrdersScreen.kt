package com.wadii.pages.provider.orders

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.pages.provider.respond.RespondToOrderScreen
import com.wadii.screens.orders.StageBadge
import com.wadii.screens.orders.isOpen
import com.wadii.screens.orders.isRepliedBy
import com.wadii.screens.orders.newestFirst
import com.wadii.screens.orders.stage
import com.wadii.state.AppState
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.InputField
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.classNames
import org.jetbrains.compose.web.dom.*

private enum class OrderFilter(val label: String) { All("All"), NeedsReply("Needs reply"), Replied("Replied"), MyJobs("My jobs") }

class ProviderOrdersScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<ProviderOrdersViewModel>()
        val state by model.state.collectAsState()
        val providerId = AppState.user?.provider?.id

        var query by remember { mutableStateOf("") }
        var filter by remember { mutableStateOf(OrderFilter.All) }

        Div(attrs = { classes("space-y-6") }) {
            Div {
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Incoming Orders") }
                P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) { Text("Newest requests first. Reply fast to win the deal.") }
            }

            when {
                state.isLoading -> LoadingSkeletons(4, "h-24")
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                state.orders.isEmpty() -> EmptyState("📦", "No orders yet", "New buyer requests will appear here.")
                else -> {
                    val replied: (com.wadii.domain.model.order.OrderModel) -> Boolean =
                        { o -> providerId != null && o.isRepliedBy(providerId) }

                    // Search + status tabs
                    InputField("Search", query, "Search by car, part or order number", leadingIcon = "🔍") { query = it }
                    Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                        OrderFilter.entries.forEach { f ->
                            val active = f == filter
                            Button(attrs = {
                                classes(*classNames(
                                    "px-4", "py-2", "min-h-11", "rounded-full", "text-sm", "font-medium", "border", "transition-all",
                                    if (active) "bg-surface shadow-neu-inset text-fg-brand border-brand-subtle"
                                    else "bg-surface shadow-neu-sm border-default text-body hover:shadow-neu-md hover:text-heading"
                                ))
                                onClick { filter = f }
                            }) { Text(f.label) }
                        }
                    }

                    val visible = state.orders.newestFirst()
                        .filter { o ->
                            when (filter) {
                                OrderFilter.All -> true
                                OrderFilter.NeedsReply -> !replied(o) && o.isOpen
                                OrderFilter.Replied -> replied(o)
                                OrderFilter.MyJobs -> providerId != null && o.acceptedProviderId == providerId
                            }
                        }
                        .filter { o ->
                            val q = query.trim()
                            q.isEmpty() || o.carModelYear.contains(q, true) || o.comment.contains(q, true) ||
                                "#${o.id}".contains(q, true) || o.spareParts.any { it.sparePartName.contains(q, true) }
                        }

                    if (visible.isEmpty()) {
                        EmptyState("🔎", "No orders match", "Try a different search or tab.")
                    } else {
                        Div(attrs = { classes("space-y-3", "stagger") }) {
                            visible.forEach { order ->
                                Div(attrs = {
                                    style { property("cursor", "pointer") }
                                    onClick { navigator.push(RespondToOrderScreen(order.id)) }
                                }) {
                                    Card(classes = "p-5 hover:shadow-neu-md active:shadow-neu-inset transition-all") {
                                        Div(attrs = { classes("flex", "items-start", "justify-between", "gap-3") }) {
                                            Div(attrs = { classes("min-w-0") }) {
                                                P(attrs = { classes("font-semibold", "text-heading") }) { Text("Order #${order.id} — ${order.carModelYear}") }
                                                P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) { Text(order.comment.take(80)) }
                                                order.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                                    Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                                        svcs.forEach { s -> Badge(s.name, variant = BadgeVariant.Brand, pill = true) }
                                                    }
                                                }
                                            }
                                            Div(attrs = { classes("text-right", "space-y-1", "flex-shrink-0") }) {
                                                P(attrs = { classes("text-xs", "text-body-subtle") }) { Text(order.date.take(10)) }
                                                when {
                                                    providerId != null && order.acceptedProviderId == providerId -> StageBadge(order.stage())
                                                    replied(order) -> Badge("You replied", variant = BadgeVariant.Success, pill = true, icon = "✓")
                                                    order.isOpen -> Badge("Needs reply", variant = BadgeVariant.Warning, pill = true, icon = "⏳")
                                                    else -> StageBadge(order.stage())
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
