package com.wadii.screens.providerDetail

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.data.api.to1dp
import com.wadii.screens.orders.new.NewOrderScreen
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Avatar
import com.wadii.ui.AvatarShape
import com.wadii.ui.AvatarSize
import com.wadii.ui.BackButton
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.LoadingScreen
import com.wadii.ui.PrimaryButton
import com.wadii.ui.SecondaryButton
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf

class ProviderDetailScreen(val providerId: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<ProviderDetailViewModel> { parametersOf(providerId) }
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            BackButton { navigator.pop() }

            when {
                state.isLoading -> LoadingScreen()
                state.error.isNotEmpty() -> Alert(variant = AlertVariant.Danger, body = state.error)

                state.provider.id != 0 -> {
                    val provider = state.provider
                    val following = state.following

                    // ── Header Card ──────────────────────────────────────────────
                    Card(classes = "overflow-hidden") {
                        Div(attrs = { classes("h-24", "bg-surface-secondary") }) {}
                        Div(attrs = { classes("px-6", "pb-6") }) {
                            Div(attrs = { classes("flex", "items-end", "justify-between", "-mt-10", "mb-4") }) {
                                Avatar(
                                    initials = provider.name.firstOrNull()?.toString() ?: "?",
                                    size = AvatarSize.XXL,
                                    shape = AvatarShape.RoundedSquare,
                                    bordered = true
                                )
                                Div(attrs = { classes("mt-10") }) {
                                    if (following) {
                                        SecondaryButton("✓ Unfollow") { model.onEvent(ProviderDetailEvent.ToggleFollow(providerId)) }
                                    } else {
                                        PrimaryButton("+ Follow") { model.onEvent(ProviderDetailEvent.ToggleFollow(providerId)) }
                                    }
                                }
                            }
                            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text(provider.name) }
                            Div(attrs = { classes("flex", "items-center", "gap-4", "mt-2", "text-sm", "text-body") }) {
                                Span { Text("⭐ ${provider.rate.to1dp()} rating") }
                                Span { Text("👥 ${provider.followersCount} followers") }
                            }
                            if (provider.services.isNotEmpty()) {
                                Div(attrs = { classes("flex", "flex-wrap", "gap-2", "mt-4") }) {
                                    provider.services.forEach { s -> Badge(s.name, variant = BadgeVariant.Brand, pill = true) }
                                }
                            }
                        }
                    }

                    // ── Links ────────────────────────────────────────────────────
                    if (provider.links.isNotEmpty()) {
                        Card(classes = "p-6") {
                            H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("🔗 Links") }
                            Div(attrs = { classes("flex", "flex-col", "gap-2") }) {
                                provider.links.forEach { link ->
                                    A(href = link.link, attrs = {
                                        classes("flex", "items-center", "gap-2", "text-fg-brand", "hover:underline", "text-sm", "break-all")
                                        attr("target", "_blank")
                                        attr("rel", "noopener noreferrer")
                                    }) { Text("↗ ${link.link}") }
                                }
                            }
                        }
                    }

                    // ── Branches ─────────────────────────────────────────────────
                    if (provider.branches.isNotEmpty()) {
                        Card(classes = "p-6") {
                            H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("📍 Branches") }
                            Div(attrs = { classes("space-y-4") }) {
                                provider.branches.forEach { b ->
                                    Div(attrs = { classes("border", "border-default", "rounded-neu-base", "p-4") }) {
                                        P(attrs = { classes("font-medium", "text-heading") }) { Text(b.name) }
                                        P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) { Text(b.address) }
                                        if (b.workTimes.isNotEmpty()) {
                                            Div(attrs = { classes("mt-3", "space-y-1") }) {
                                                b.workTimes.forEach { wt ->
                                                    Div(attrs = { classes("flex", "justify-between", "text-xs", "text-body") }) {
                                                        Span { Text(wt.day) }
                                                        Span(attrs = { classes("font-medium", "text-heading") }) { Text(value = "${wt.startTime} – ${wt.closeTime}") }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Offers ───────────────────────────────────────────────────
                    if (provider.offers.isNotEmpty()) {
                        Card(classes = "p-6") {
                            H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("🎁 Offers") }
                            Div(attrs = { classes("space-y-3") }) {
                                provider.offers.forEach { offer ->
                                    Div(attrs = { classes("border", "border-brand-subtle", "bg-brand-softer", "rounded-neu-base", "p-4") }) {
                                        Div(attrs = { classes("flex", "justify-between", "items-start", "gap-2") }) {
                                            P(attrs = { classes("font-semibold", "text-heading") }) { Text(offer.title) }
                                            Span(attrs = { classes("text-xs", "text-body-subtle", "shrink-0") }) { Text("Until ${offer.endDate}") }
                                        }
                                        if (offer.description.isNotEmpty()) {
                                            P(attrs = { classes("text-sm", "text-body", "mt-1") }) { Text(offer.description) }
                                        }
                                        if (offer.services.isNotEmpty()) {
                                            Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                                offer.services.forEach { s -> Badge(s.name, variant = BadgeVariant.Alternative, pill = true) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Followers ────────────────────────────────────────────────
                    if (provider.followers.isNotEmpty()) {
                        Card(classes = "p-6") {
                            H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("👥 Followers (${provider.followersCount})") }
                            Div(attrs = { classes("flex", "flex-wrap", "gap-3") }) {
                                provider.followers.forEach { follower ->
                                    Div(attrs = { classes("flex", "items-center", "gap-2") }) {
                                        Avatar(initials = follower.user.firstName.firstOrNull()?.toString() ?: "?", size = AvatarSize.SM)
                                        Span(attrs = { classes("text-sm", "text-body") }) {
                                            Text("${follower.user.firstName} ${follower.user.lastName}")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Reviews ──────────────────────────────────────────────────
                    if (provider.rates.isNotEmpty()) {
                        Card(classes = "p-6") {
                            H2(attrs = { classes("font-semibold", "text-heading", "mb-3") }) { Text("⭐ Reviews") }
                            Div(attrs = { classes("space-y-3") }) {
                                provider.rates.forEach { rateItem ->
                                    Div(attrs = { classes("border-b", "border-light", "pb-3", "last:border-0") }) {
                                        Div(attrs = { classes("flex", "items-center", "gap-1") }) {
                                            val filled = rateItem.rate.toInt()
                                            repeat(5) { i ->
                                                Span(attrs = { classes(if (i < filled) "text-warning" else "text-fg-disabled") }) { Text("★") }
                                            }
                                            Span(attrs = { classes("text-sm", "text-body-subtle", "ml-1") }) { Text(rateItem.rate.to1dp()) }
                                        }
                                        if (rateItem.comment.isNotEmpty()) {
                                            P(attrs = { classes("text-sm", "text-body", "mt-1") }) { Text(rateItem.comment) }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Request Service CTA ──────────────────────────────────────
                    Card(classes = "p-6") {
                        PrimaryButton("Request Service", fullWidth = true) { navigator.push(NewOrderScreen()) }
                    }
                }
            }
        }
    }
}
