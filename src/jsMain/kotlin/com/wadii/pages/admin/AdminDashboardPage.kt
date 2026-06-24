package com.wadii.pages.admin

import androidx.compose.runtime.*
import com.wadii.navigation.Screen
import com.wadii.navigation.LocalNavigator
import com.wadii.navigation.currentOrThrow
import com.wadii.screens.AdsScreen
import com.wadii.screens.ProviderRequestsScreen
import com.wadii.screens.ServicesScreen
import com.wadii.ui.LoadingScreen
import com.wadii.viewmodel.AdminDashboardEvent
import com.wadii.viewmodel.AdminDashboardScreenModel
import com.wadii.viewmodel.UiState
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.dom.*

@Composable
fun AdminDashboardPage() {
    val navigator = LocalNavigator.currentOrThrow
    val model = rememberScreenModel { AdminDashboardScreenModel() }

    Div(attrs = { classes("space-y-8") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Admin Dashboard") }

        when (val s = model.state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) { Text(s.message) }
            is UiState.Success -> {
                val d = s.data
                Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-3", "gap-4") }) {
                    StatButton("Provider Requests", d.requestCount, "📋", ProviderRequestsScreen)
                    StatButton("Advertisements", d.adCount, "📣", AdsScreen)
                    StatButton("Services", d.serviceCount, "🔧", ServicesScreen)
                }
                Div {
                    Div(attrs = { classes("flex", "items-center", "justify-between", "mb-3") }) {
                        H2(attrs = { classes("font-semibold", "text-slate-800") }) { Text("Pending Provider Requests") }
                        Button(attrs = {
                            classes("text-sm", "text-amber-600", "hover:underline")
                            onClick { navigator.replaceAll(ProviderRequestsScreen) }
                        }) { Text("View all") }
                    }
                    if (d.recentRequests.isEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-xl", "p-8", "text-center", "text-slate-500", "text-sm") }) { Text("No pending requests.") }
                    } else {
                        Div(attrs = { classes("space-y-2") }) {
                            d.recentRequests.forEach { req ->
                                Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-4", "flex", "items-center", "justify-between") }) {
                                    Div {
                                        P(attrs = { classes("font-medium", "text-slate-800") }) { Text(req.name) }
                                        P(attrs = { classes("text-sm", "text-slate-500") }) { Text("📞 ${req.phoneNumber} · 📍 ${req.address}") }
                                    }
                                    Button(attrs = {
                                        classes("text-sm", "text-amber-600", "font-medium", "hover:underline")
                                        onClick { navigator.replaceAll(ProviderRequestsScreen) }
                                    }) { Text("Review →") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatButton(label: String, value: Int, emoji: String, screen: Screen) {
    val navigator = LocalNavigator.currentOrThrow
    Div(attrs = {
        classes("bg-white", "rounded-2xl", "p-6", "shadow-sm", "hover:shadow-md", "transition-shadow", "cursor-pointer")
        onClick { navigator.replaceAll(screen) }
    }) {
        Div(attrs = { classes("inline-flex", "p-3", "bg-amber-50", "rounded-xl", "mb-3") }) {
            Span(attrs = { classes("text-xl") }) { Text(emoji) }
        }
        P(attrs = { classes("text-3xl", "font-bold", "text-slate-800") }) { Text(value.toString()) }
        P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(label) }
    }
}
