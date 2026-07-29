package com.wadii.pages.admin.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.pages.admin.ads.AdsScreen
import com.wadii.pages.admin.provider.ProviderRequestsScreen
import com.wadii.pages.admin.service.ServicesScreen
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.LoadingScreen
import com.wadii.ui.StatCard
import org.jetbrains.compose.web.dom.*

class AdminDashboardScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val adminDashboardScreenModel =
            navigator.koinNavigatorScreenModel<AdminDashboardScreenModel>()
        val state by adminDashboardScreenModel.state.collectAsState()
        Div(attrs = { classes("space-y-8") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Admin Dashboard") }
            if (state.isLoading) {
                LoadingScreen()
            }
            if (state.requests.isNotEmpty()) {
                Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-3", "gap-4") }) {
                    StatCard("Seller Requests", state.requests.size.toString(), "📋") {
                        navigator.replaceAll(ProviderRequestsScreen())
                    }
                    StatCard("Advertisements", state.ads.size.toString(), "📣") {
                        navigator.replaceAll(AdsScreen())
                    }
                    StatCard("Categories", state.services.size.toString(), "⚙️") {
                        navigator.replaceAll(ServicesScreen())
                    }
                }
                Div {
                    Div(attrs = { classes("flex", "items-center", "justify-between", "mb-3") }) {
                        H2(attrs = { classes("font-semibold", "text-heading") }) { Text("Pending Seller Requests") }
                        GhostButton("View all") { navigator.replaceAll(ProviderRequestsScreen()) }
                    }
                    if (state.requests.isEmpty()) {
                        EmptyState("📋", "No pending requests.")
                    } else {
                        Div(attrs = { classes("space-y-2") }) {
                            state.requests.forEach { req ->
                                Card(classes = "p-4 flex items-center justify-between") {
                                    Div {
                                        P(attrs = { classes("font-medium", "text-heading") }) { Text(req.name) }
                                        P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("📞 ${req.phoneNumber} · 📍 ${req.address}") }
                                    }
                                    GhostButton("Review →") { navigator.replaceAll(ProviderRequestsScreen()) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
