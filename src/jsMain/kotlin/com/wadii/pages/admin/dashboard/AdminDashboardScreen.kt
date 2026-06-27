package com.wadii.pages.admin.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.pages.admin.ads.AdsScreen
import com.wadii.pages.admin.provider.ProviderRequestsScreen
import com.wadii.pages.admin.service.ServicesScreen
import com.wadii.ui.LoadingScreen
import org.jetbrains.compose.web.dom.*

class AdminDashboardScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val adminDashboardScreenModel =
            navigator.koinNavigatorScreenModel<AdminDashboardScreenModel>()
        val state by adminDashboardScreenModel.state.collectAsState()
        Div(attrs = { classes("space-y-8") }) {
            H1(attrs = {
                classes(
                    "text-2xl",
                    "font-bold",
                    "text-slate-800"
                )
            }) { Text("Admin Dashboard") }
            if (state.isLoading) {
                LoadingScreen()
            }
            if (state.requests.isNotEmpty()) {
                Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-3", "gap-4") }) {
                    StatButton(
                        "Provider Requests",
                        state.requests.size,
                        "📋",
                        ProviderRequestsScreen()
                    )
                    StatButton(
                        "Advertisements",
                        state.ads.size,
                        "📣",
                        AdsScreen()
                    )
                    StatButton(
                        "Services",
                        state.services.size,
                        "🔧",
                        ServicesScreen()
                    )
                }
                Div {
                    Div(attrs = {
                        classes(
                            "flex",
                            "items-center",
                            "justify-between",
                            "mb-3"
                        )
                    }) {
                        H2(attrs = {
                            classes(
                                "font-semibold",
                                "text-slate-800"
                            )
                        }) { Text("Pending Provider Requests") }
                        Button(attrs = {
                            classes("text-sm", "text-amber-600", "hover:underline")
                            onClick { navigator.replaceAll(ProviderRequestsScreen()) }
                        }) { Text("View all") }
                    }
                    if (state.requests.isEmpty()) {
                        Div(attrs = {
                            classes(
                                "bg-white",
                                "rounded-xl",
                                "p-8",
                                "text-center",
                                "text-slate-500",
                                "text-sm"
                            )
                        }) { Text("No pending requests.") }
                    } else {
                        Div(attrs = { classes("space-y-2") }) {
                            state.requests.forEach { req ->
                                Div(attrs = {
                                    classes(
                                        "bg-white",
                                        "border",
                                        "border-slate-200",
                                        "rounded-xl",
                                        "p-4",
                                        "flex",
                                        "items-center",
                                        "justify-between"
                                    )
                                }) {
                                    Div {
                                        P(attrs = {
                                            classes(
                                                "font-medium",
                                                "text-slate-800"
                                            )
                                        }) { Text(req.name) }
                                        P(attrs = {
                                            classes(
                                                "text-sm",
                                                "text-slate-500"
                                            )
                                        }) { Text("📞 ${req.phoneNumber} · 📍 ${req.address}") }
                                    }
                                    Button(attrs = {
                                        classes(
                                            "text-sm",
                                            "text-amber-600",
                                            "font-medium",
                                            "hover:underline"
                                        )
                                        onClick { navigator.replaceAll(ProviderRequestsScreen()) }
                                    }) { Text("Review →") }
                                }
                            }
                        }
                    }
                }
            }
//                    Div(attrs = {
//                    classes(
//                        "bg-white",
//                        "rounded-xl",
//                        "p-12",
//                        "text-center",
//                        "text-slate-500"
//                    )
//                }) { Text(s.message) }
        }
    }
}

@Composable
private fun StatButton(label: String, value: Int, emoji: String, screen: Screen) {
    val navigator = LocalNavigator.currentOrThrow
    Div(attrs = {
        classes(
            "bg-white",
            "rounded-2xl",
            "p-6",
            "shadow-sm",
            "hover:shadow-md",
            "transition-shadow",
            "cursor-pointer"
        )
        onClick { navigator.replaceAll(screen) }
    }) {
        Div(attrs = { classes("inline-flex", "p-3", "bg-amber-50", "rounded-xl", "mb-3") }) {
            Span(attrs = { classes("text-xl") }) { Text(emoji) }
        }
        P(attrs = { classes("text-3xl", "font-bold", "text-slate-800") }) { Text(value.toString()) }
        P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(label) }
    }
}
