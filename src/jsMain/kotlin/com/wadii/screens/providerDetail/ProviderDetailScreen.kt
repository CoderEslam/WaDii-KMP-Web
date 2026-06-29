package com.wadii.screens.providerDetail

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.components.BackButton
import com.wadii.data.api.to1dp
import com.wadii.screens.orders.new.NewOrderScreen
import com.wadii.ui.LoadingScreen
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf

class ProviderDetailScreen(val providerId: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<ProviderDetailViewModel> { parametersOf(providerId) }
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            BackButton {
                navigator.pop()
            }
//            Button(attrs = {
//                classes("text-sm", "text-slate-500", "hover:text-slate-700")
//                onClick { navigator.pop() }
//            }) { Text("← Back") }

            when {
                state.isLoading -> LoadingScreen()
                state.error.isNotEmpty() -> Div(attrs = {
                    classes(
                        "bg-white",
                        "rounded-2xl",
                        "p-12",
                        "text-center",
                        "text-slate-500"
                    )
                }) { Text(state.error) }

                state.provider.id != 0 -> {
                    val provider = state.provider
                    val following = state.following

                    // ── Header Card ──────────────────────────────────────────────
                    Div(attrs = {
                        classes(
                            "bg-white",
                            "rounded-2xl",
                            "overflow-hidden",
                            "shadow-sm"
                        )
                    }) {
                        Div(attrs = {
                            classes(
                                "h-32",
                                "bg-gradient-to-r",
                                "from-amber-400",
                                "to-orange-400"
                            )
                        }) {}
                        Div(attrs = { classes("px-6", "pb-6") }) {
                            Div(attrs = {
                                classes(
                                    "flex",
                                    "items-end",
                                    "justify-between",
                                    "-mt-10",
                                    "mb-4"
                                )
                            }) {
                                Div(attrs = {
                                    classes(
                                        "w-20",
                                        "h-20",
                                        "rounded-2xl",
                                        "bg-white",
                                        "border-4",
                                        "border-white",
                                        "shadow",
                                        "flex",
                                        "items-center",
                                        "justify-center",
                                        "text-3xl",
                                        "font-bold",
                                        "text-amber-600"
                                    )
                                }) {
                                    Text(provider.name.firstOrNull()?.toString() ?: "?")
                                }
                                Button(attrs = {
                                    classes(
                                        "flex",
                                        "items-center",
                                        "gap-2",
                                        "px-4",
                                        "py-2",
                                        "rounded-xl",
                                        "text-sm",
                                        "font-medium",
                                        "transition-colors",
                                        "mt-10"
                                    )
                                    if (following) classes(
                                        "bg-slate-100",
                                        "text-slate-700",
                                        "hover:bg-slate-200"
                                    )
                                    else classes("bg-amber-500", "text-white", "hover:bg-amber-600")
                                    onClick {
                                        model.onEvent(
                                            ProviderDetailEvent.ToggleFollow(
                                                providerId
                                            )
                                        )
                                    }
                                }) { Text(if (following) "✓ Unfollow" else "+ Follow") }
                            }
                            H1(attrs = {
                                classes(
                                    "text-2xl",
                                    "font-bold",
                                    "text-slate-800"
                                )
                            }) { Text(provider.name) }
                            Div(attrs = {
                                classes(
                                    "flex",
                                    "items-center",
                                    "gap-4",
                                    "mt-2",
                                    "text-sm",
                                    "text-slate-600"
                                )
                            }) {
                                Span { Text("⭐ ${provider.rate.to1dp()} rating") }
                                Span { Text("👥 ${provider.followersCount} followers") }
                            }
                            if (provider.services.isNotEmpty()) {
                                Div(attrs = { classes("flex", "flex-wrap", "gap-2", "mt-4") }) {
                                    provider.services.forEach { s ->
                                        Span(attrs = {
                                            classes(
                                                "px-3",
                                                "py-1",
                                                "bg-amber-50",
                                                "text-amber-700",
                                                "text-sm",
                                                "rounded-full"
                                            )
                                        }) { Text(s.name) }
                                    }
                                }
                            }
                        }
                    }

                    // ── Links ────────────────────────────────────────────────────
                    if (provider.links.isNotEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                            H2(attrs = {
                                classes(
                                    "font-semibold",
                                    "text-slate-800",
                                    "mb-3"
                                )
                            }) { Text("🔗 Links") }
                            Div(attrs = { classes("flex", "flex-col", "gap-2") }) {
                                provider.links.forEach { link ->
                                    A(href = link.link, attrs = {
                                        classes(
                                            "flex",
                                            "items-center",
                                            "gap-2",
                                            "text-amber-600",
                                            "hover:text-amber-700",
                                            "text-sm",
                                            "break-all"
                                        )
                                        attr("target", "_blank")
                                        attr("rel", "noopener noreferrer")
                                    }) { Text("↗ ${link.link}") }
                                }
                            }
                        }
                    }

                    // ── Branches ─────────────────────────────────────────────────
                    if (provider.branches.isNotEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                            H2(attrs = {
                                classes(
                                    "font-semibold",
                                    "text-slate-800",
                                    "mb-3"
                                )
                            }) { Text("📍 Branches") }
                            Div(attrs = { classes("space-y-4") }) {
                                provider.branches.forEach { b ->
                                    Div(attrs = {
                                        classes(
                                            "border",
                                            "border-slate-100",
                                            "rounded-xl",
                                            "p-4"
                                        )
                                    }) {
                                        P(attrs = {
                                            classes(
                                                "font-medium",
                                                "text-slate-800"
                                            )
                                        }) { Text(b.name) }
                                        P(attrs = {
                                            classes(
                                                "text-sm",
                                                "text-slate-500",
                                                "mt-1"
                                            )
                                        }) { Text(b.address) }
                                        if (b.workTimes.isNotEmpty()) {
                                            Div(attrs = { classes("mt-3", "space-y-1") }) {
                                                b.workTimes.forEach { wt ->
                                                    Div(attrs = {
                                                        classes(
                                                            "flex",
                                                            "justify-between",
                                                            "text-xs",
                                                            "text-slate-600"
                                                        )
                                                    }) {
                                                        Span { Text(wt.day) }
                                                        Span(attrs = {
                                                            classes(
                                                                "font-medium",
                                                                "text-slate-800"
                                                            )
                                                        }) { Text(value = "${wt.startTime} – ${wt.closeTime}") }
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
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                            H2(attrs = {
                                classes(
                                    "font-semibold",
                                    "text-slate-800",
                                    "mb-3"
                                )
                            }) { Text("🎁 Offers") }
                            Div(attrs = { classes("space-y-3") }) {
                                provider.offers.forEach { offer ->
                                    Div(attrs = {
                                        classes(
                                            "border",
                                            "border-amber-100",
                                            "bg-amber-50",
                                            "rounded-xl",
                                            "p-4"
                                        )
                                    }) {
                                        Div(attrs = {
                                            classes(
                                                "flex",
                                                "justify-between",
                                                "items-start",
                                                "gap-2"
                                            )
                                        }) {
                                            P(attrs = {
                                                classes(
                                                    "font-semibold",
                                                    "text-slate-800"
                                                )
                                            }) { Text(offer.title) }
                                            Span(attrs = {
                                                classes(
                                                    "text-xs",
                                                    "text-slate-400",
                                                    "shrink-0"
                                                )
                                            }) { Text("Until ${offer.endDate}") }
                                        }
                                        if (offer.description.isNotEmpty()) {
                                            P(attrs = {
                                                classes(
                                                    "text-sm",
                                                    "text-slate-600",
                                                    "mt-1"
                                                )
                                            }) { Text(offer.description) }
                                        }
                                        if (offer.services.isNotEmpty()) {
                                            Div(attrs = {
                                                classes(
                                                    "flex",
                                                    "flex-wrap",
                                                    "gap-1",
                                                    "mt-2"
                                                )
                                            }) {
                                                offer.services.forEach { s ->
                                                    Span(attrs = {
                                                        classes(
                                                            "px-2",
                                                            "py-1",
                                                            "bg-white",
                                                            "text-amber-700",
                                                            "text-xs",
                                                            "rounded-full",
                                                            "border",
                                                            "border-amber-200"
                                                        )
                                                    }) { Text(s.name) }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Followers ────────────────────────────────────────────────
                    if (provider.followers.isNotEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                            H2(attrs = {
                                classes(
                                    "font-semibold",
                                    "text-slate-800",
                                    "mb-3"
                                )
                            }) { Text("👥 Followers (${provider.followersCount})") }
                            Div(attrs = { classes("flex", "flex-wrap", "gap-3") }) {
                                provider.followers.forEach { follower ->
                                    Div(attrs = { classes("flex", "items-center", "gap-2") }) {
                                        Div(attrs = {
                                            classes(
                                                "w-8",
                                                "h-8",
                                                "rounded-full",
                                                "bg-amber-100",
                                                "flex",
                                                "items-center",
                                                "justify-center",
                                                "text-xs",
                                                "font-bold",
                                                "text-amber-700"
                                            )
                                        }) {
                                            Text(
                                                follower.user.firstName.firstOrNull()?.toString()
                                                    ?: "?"
                                            )
                                        }
                                        Span(attrs = { classes("text-sm", "text-slate-700") }) {
                                            Text("${follower.user.firstName} ${follower.user.lastName}")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Reviews ──────────────────────────────────────────────────
                    if (provider.rates.isNotEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                            H2(attrs = {
                                classes(
                                    "font-semibold",
                                    "text-slate-800",
                                    "mb-3"
                                )
                            }) { Text("⭐ Reviews") }
                            Div(attrs = { classes("space-y-3") }) {
                                provider.rates.forEach { rateItem ->
                                    Div(attrs = {
                                        classes(
                                            "border-b",
                                            "border-slate-50",
                                            "pb-3",
                                            "last:border-0"
                                        )
                                    }) {
                                        Div(attrs = { classes("flex", "items-center", "gap-1") }) {
                                            val filled = rateItem.rate.toInt()
                                            repeat(5) { i ->
                                                Span(attrs = { classes(if (i < filled) "text-amber-400" else "text-slate-200") }) {
                                                    Text(
                                                        "★"
                                                    )
                                                }
                                            }
                                            Span(attrs = {
                                                classes(
                                                    "text-sm",
                                                    "text-slate-500",
                                                    "ml-1"
                                                )
                                            }) { Text(rateItem.rate.to1dp()) }
                                        }
                                        if (rateItem.comment.isNotEmpty()) {
                                            P(attrs = {
                                                classes(
                                                    "text-sm",
                                                    "text-slate-600",
                                                    "mt-1"
                                                )
                                            }) { Text(rateItem.comment) }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Request Service CTA ──────────────────────────────────────
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                        Button(attrs = {
                            classes(
                                "w-full",
                                "py-3",
                                "bg-amber-500",
                                "text-white",
                                "font-semibold",
                                "rounded-xl",
                                "hover:bg-amber-600",
                                "transition-colors"
                            )
                            onClick { navigator.push(NewOrderScreen()) }
                        }) { Text("Request Service") }
                    }
                }
            }
        }
    }
}