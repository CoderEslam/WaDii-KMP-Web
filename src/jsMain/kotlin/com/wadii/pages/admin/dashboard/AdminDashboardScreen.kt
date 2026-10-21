package com.wadii.pages.admin.dashboard

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.pages.admin.ads.AdsScreen
import com.wadii.pages.admin.provider.ProviderRequestsScreen
import com.wadii.pages.admin.service.ServicesScreen
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.LoadingSkeletons
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
            Div {
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Admin Dashboard") }
                P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) { Text("Here's what needs your attention today.") }
            }

            if (state.message.isNotBlank()) {
                Alert(variant = AlertVariant.Warning, heading = "Some numbers couldn't be loaded", body = state.message)
            }

            // Always rendered, even when everything is zero, so the page is never blank.
            // While data is still arriving the cards show skeletons instead of misleading zeros.
            if (state.isLoading && state.requests.isEmpty() && state.ads.isEmpty() && state.services.isEmpty()) {
                LoadingSkeletons(3, "h-28")
            } else {
                Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-3", "gap-4", "stagger") }) {
                    StatCard("Pending Seller Requests", state.requests.size.toString(), "📋", "bg-warning-soft") {
                        navigator.replaceAll(ProviderRequestsScreen())
                    }
                    StatCard("Advertisements", state.ads.size.toString(), "📣") {
                        navigator.replaceAll(AdsScreen())
                    }
                    StatCard("Categories", state.services.size.toString(), "⚙️", "bg-success-soft") {
                        navigator.replaceAll(ServicesScreen())
                    }
                }
            }

            Div {
                Div(attrs = { classes("flex", "items-center", "justify-between", "mb-3") }) {
                    H2(attrs = { classes("font-semibold", "text-heading") }) { Text("Pending Seller Requests") }
                    GhostButton("View all") { navigator.replaceAll(ProviderRequestsScreen()) }
                }
                if (state.requests.isEmpty()) {
                    EmptyState("🎉", "You're all caught up", "New seller requests will show up here.")
                } else {
                    Div(attrs = { classes("space-y-2", "stagger") }) {
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
