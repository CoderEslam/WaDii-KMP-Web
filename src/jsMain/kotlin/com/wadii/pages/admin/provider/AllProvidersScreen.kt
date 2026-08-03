package com.wadii.pages.admin.provider

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.LoadingScreen
import com.wadii.utils.Constants.BASE_URL_USER_IMAGES
import org.jetbrains.compose.web.dom.*

class AllProvidersScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<AllProvidersViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            Div(attrs = { classes("flex", "items-center", "justify-between") }) {
                H1(attrs = {
                    classes(
                        "text-2xl",
                        "font-semibold",
                        "text-heading"
                    )
                }) { Text("All Providers") }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)

                else -> {
                    val providers = state.providers
                    if (providers.isEmpty()) {
                        EmptyState("🧰", "No providers found.")
                    } else {
                        Div(attrs = { classes("space-y-4") }) {
                            providers.forEach { provider ->
                                Card(classes = "p-6") {
                                    Div(attrs = {
                                        classes(
                                            "flex",
                                            "items-start",
                                            "justify-between",
                                            "gap-4"
                                        )
                                    }) {
                                        Div(attrs = { classes("flex", "items-start", "gap-4") }) {
                                            val img = provider.user.image
                                            if (img != null) {
                                                Img(
                                                    src = "$BASE_URL_USER_IMAGES/$img",
                                                    attrs = {
                                                        classes(
                                                            "w-12",
                                                            "h-12",
                                                            "rounded-full",
                                                            "object-cover",
                                                            "border",
                                                            "border-default"
                                                        )
                                                    }
                                                )
                                            } else {
                                                Div(attrs = {
                                                    classes(
                                                        "w-12",
                                                        "h-12",
                                                        "rounded-full",
                                                        "bg-brand-softer",
                                                        "flex",
                                                        "items-center",
                                                        "justify-center",
                                                        "text-fg-brand-strong",
                                                        "font-semibold"
                                                    )
                                                }) { Text(provider.name.take(1).uppercase()) }
                                            }
                                            Div(attrs = { classes("space-y-1") }) {
                                                H3(attrs = {
                                                    classes(
                                                        "font-semibold",
                                                        "text-heading",
                                                        "text-lg"
                                                    )
                                                }) { Text(provider.name) }
                                                P(attrs = {
                                                    classes(
                                                        "text-sm",
                                                        "text-body-subtle"
                                                    )
                                                }) { Text("📞 ${provider.user.phone}") }
                                                if (provider.user.email.isNotBlank()) {
                                                    P(attrs = {
                                                        classes(
                                                            "text-sm",
                                                            "text-body-subtle"
                                                        )
                                                    }) { Text("✉️ ${provider.user.email}") }
                                                }
                                                provider.branches.takeIf { it.isNotEmpty() }?.let { branches ->
                                                    branches.forEach { branch ->
                                                        P(attrs = {
                                                            classes(
                                                                "text-sm",
                                                                "text-body-subtle"
                                                            )
                                                        }) { Text("📍 ${branch.name} · ${branch.address}") }
                                                    }
                                                }
                                                provider.services.takeIf { it.isNotEmpty() }?.let { svcs ->
                                                    Div(attrs = {
                                                        classes(
                                                            "flex",
                                                            "flex-wrap",
                                                            "gap-1",
                                                            "mt-2"
                                                        )
                                                    }) {
                                                        svcs.forEach { s ->
                                                            Span(attrs = {
                                                                classes(
                                                                    "px-2",
                                                                    "py-0.5",
                                                                    "bg-warning-soft",
                                                                    "text-fg-warning",
                                                                    "text-xs",
                                                                    "rounded-full"
                                                                )
                                                            }) { Text(s.name) }
                                                        }
                                                    }
                                                }
                                                provider.links.takeIf { it.isNotEmpty() }?.let { links ->
                                                    Div(attrs = { classes("space-y-1", "mt-1") }) {
                                                        links.forEach { link ->
                                                            A(
                                                                href = link.link,
                                                                attrs = {
                                                                    classes(
                                                                        "text-sm",
                                                                        "text-fg-warning",
                                                                        "hover:underline",
                                                                        "block"
                                                                    )
                                                                    attr("target", "_blank")
                                                                    attr("rel", "noopener noreferrer")
                                                                }
                                                            ) { Text("🔗 ${link.link}") }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Div(attrs = {
                                            classes(
                                                "flex",
                                                "flex-col",
                                                "items-end",
                                                "gap-1",
                                                "shrink-0"
                                            )
                                        }) {
                                            Span(attrs = {
                                                classes(
                                                    "text-sm",
                                                    "font-semibold",
                                                    "text-heading"
                                                )
                                            }) { Text("⭐ ${provider.rate}") }
                                            Span(attrs = {
                                                classes(
                                                    "text-xs",
                                                    "text-body-subtle"
                                                )
                                            }) { Text("${provider.followersCount} followers") }
                                            GhostButton("✏️ Edit") {
                                                navigator.push(EditProviderScreen(provider))
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
