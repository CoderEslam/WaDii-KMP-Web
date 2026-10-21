package com.wadii.screens.orders

import androidx.compose.runtime.Composable
import com.wadii.domain.model.order.OrderModel
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.classNames
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/**
 * Where an order stands, straight from the server's `OrderStatus`:
 * PENDING → RESPONSES_AVAILABLE → ACCEPTED → IN_PROGRESS → COMPLETED, or CANCELED at any point before the job starts.
 */
enum class OrderStage(val label: String, val icon: String, val variant: BadgeVariant) {
    Waiting("Waiting for proposals", "⏳", BadgeVariant.Warning),
    HasProposals("Proposals received", "💌", BadgeVariant.Brand),
    Accepted("Deal accepted", "🤝", BadgeVariant.Success),
    InProgress("Work in progress", "🔧", BadgeVariant.Brand),
    Completed("Completed", "🏁", BadgeVariant.Success),
    Cancelled("Cancelled", "✕", BadgeVariant.Danger)
}

fun OrderModel.stage(): OrderStage = when (status.uppercase()) {
    "CANCELED", "CANCELLED" -> OrderStage.Cancelled
    "COMPLETED" -> OrderStage.Completed
    "IN_PROGRESS" -> OrderStage.InProgress
    "ACCEPTED" -> OrderStage.Accepted
    "RESPONSES_AVAILABLE" -> OrderStage.HasProposals
    "PENDING" -> OrderStage.Waiting
    // Unknown or missing status: fall back to what the responses say.
    else -> when {
        responses.any { it.isAccepted } -> OrderStage.Accepted
        responses.isNotEmpty() -> OrderStage.HasProposals
        else -> OrderStage.Waiting
    }
}

// ---- what each party may do, per API_REFERENCE.md "Order lifecycle" ----

/** Providers may respond and the customer may accept or decline only while the order is open. */
val OrderModel.isOpen: Boolean get() = stage() == OrderStage.Waiting || stage() == OrderStage.HasProposals

/** Editing is allowed only before anyone has responded. */
val OrderModel.canEdit: Boolean get() = stage() == OrderStage.Waiting

/** The owner can cancel until the work starts. */
val OrderModel.canCancel: Boolean
    get() = stage().let { it == OrderStage.Waiting || it == OrderStage.HasProposals || it == OrderStage.Accepted }

/** Deleting is allowed for orders nobody answered and for canceled ones. */
val OrderModel.canDelete: Boolean get() = stage().let { it == OrderStage.Waiting || it == OrderStage.Cancelled }

/** The provider starts an accepted order. */
val OrderModel.canStart: Boolean get() = stage() == OrderStage.Accepted

/** The customer can complete an accepted or in-progress order, which unlocks rating. */
val OrderModel.canComplete: Boolean get() = stage().let { it == OrderStage.Accepted || it == OrderStage.InProgress }

/** Chat and calls exist only between a customer and the provider of an accepted order. */
val OrderModel.chatUnlocked: Boolean
    get() = stage().let { it == OrderStage.Accepted || it == OrderStage.InProgress || it == OrderStage.Completed }

val OrderModel.OrderResponse.isPending: Boolean get() = state.equals("PENDING", ignoreCase = true) || state.isBlank()
val OrderModel.OrderResponse.isAccepted: Boolean get() = state.equals("ACCEPTED", ignoreCase = true)
val OrderModel.OrderResponse.isDeclined: Boolean get() = state.equals("DECLINED", ignoreCase = true)
val OrderModel.OrderResponse.isWithdrawn: Boolean get() = state.equals("CANCELED", ignoreCase = true)

/** The response that won the order, if any. */
val OrderModel.acceptedResponse: OrderModel.OrderResponse?
    get() = responses.firstOrNull { it.id.toLong() == acceptedResponseId } ?: responses.firstOrNull { it.isAccepted }

/** Newest first. ISO dates sort chronologically as plain strings; id breaks ties. */
fun List<OrderModel>.newestFirst(): List<OrderModel> =
    sortedWith(compareByDescending<OrderModel> { it.date }.thenByDescending { it.id })

fun OrderModel.isRepliedBy(providerId: Long): Boolean = responses.any { it.provider.id == providerId }

@Composable
fun StageBadge(stage: OrderStage) = Badge(stage.label, variant = stage.variant, pill = true, icon = stage.icon)

/** Dots: posted → proposals → accepted → in progress → done. Cancelled orders show no progress. */
@Composable
fun OrderProgress(stage: OrderStage) {
    if (stage == OrderStage.Cancelled) return
    val steps = listOf("Posted", "Proposals", "Accepted", "In progress", "Done")
    val current = when (stage) {
        OrderStage.Waiting -> 0
        OrderStage.HasProposals -> 1
        OrderStage.Accepted -> 2
        OrderStage.InProgress -> 3
        else -> steps.lastIndex
    }
    Div(attrs = { classes("flex", "items-center", "gap-2", "flex-wrap") }) {
        steps.forEachIndexed { i, label ->
            Div(attrs = { classes("flex", "items-center", "gap-1.5") }) {
                Span(attrs = {
                    val state = if (i < current || current == steps.lastIndex) "done" else if (i == current) "current" else null
                    classes(*classNames("status-dot", state))
                }) {}
                Span(attrs = { classes("text-xs", if (i <= current) "text-heading" else "text-body-subtle") }) { Text(label) }
            }
            if (i < steps.lastIndex) Span(attrs = { classes("text-body-subtle", "text-xs") }) { Text("›") }
        }
    }
}
