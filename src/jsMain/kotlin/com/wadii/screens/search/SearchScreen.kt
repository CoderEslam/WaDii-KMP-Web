package com.wadii.screens.search

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.data.api.to1dp
import com.wadii.screens.home.OfferCard
import com.wadii.screens.providerDetail.ProviderDetailScreen
import com.wadii.ui.Avatar
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.PrimaryButton
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*

class SearchScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<SearchViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Search") }

            Div(attrs = { classes("flex", "gap-3") }) {
                Div(attrs = { classes("flex-1", "relative") }) {
                    Span(attrs = { classes("absolute", "left-4", "top-1/2", "-translate-y-1/2", "text-body", "pointer-events-none") }) { Text("🔍") }
                    Input(type = InputType.Text, attrs = {
                        classes(
                            "w-full", "pl-10", "pr-4", "py-3", "border", "border-default-medium", "rounded-neu-base",
                            "bg-surface", "shadow-neu-inset", "text-sm", "text-heading",
                            "focus:outline-none", "focus:ring-1", "focus:ring-brand", "focus:border-brand"
                        )
                        attr("placeholder", "Search parts sellers, deals, categories…")
                        attr("value", state.query)
                        onInput { model.onEvent(SearchEvent.SetQuery(it.value)) }
                        onKeyDown { if (it.key == "Enter") model.onEvent(SearchEvent.Search) }
                    })
                }
                PrimaryButton("Search", loading = state.searching) { model.onEvent(SearchEvent.Search) }
            }

            state.results?.let { r ->
                val total = r.providers.size + r.offers.size + r.services.size + r.branches.size
                if (total == 0) {
                    EmptyState("🔍", "No results found for \"${state.query}\"")
                }
                if (r.providers.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-base", "font-semibold", "text-heading", "mb-3") }) { Text("Parts Sellers") }
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                            r.providers.forEach { p ->
                                Div(attrs = {
                                    style { property("cursor", "pointer") }
                                    onClick { navigator.push(ProviderDetailScreen(p.id)) }
                                }) {
                                    Card(classes = "p-4 hover:shadow-neu-md transition-all flex items-center gap-4") {
                                        Avatar(initials = p.name.firstOrNull()?.toString() ?: "?")
                                        Div(attrs = { classes("flex-1") }) {
                                            P(attrs = { classes("font-semibold", "text-heading") }) { Text(p.name) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("⭐ ${p.rate.to1dp()} · ${p.followersCount} followers") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (r.offers.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-base", "font-semibold", "text-heading", "mb-3") }) { Text("Deals") }
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                            r.offers.forEach { OfferCard(navigator = navigator, offer = it) }
                        }
                    }
                }
                if (r.services.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-base", "font-semibold", "text-heading", "mb-3") }) { Text("Categories") }
                        Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                            r.services.forEach { svc -> Badge(svc.name, variant = BadgeVariant.Alternative, large = true, pill = true) }
                        }
                    }
                }
                if (r.branches.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-base", "font-semibold", "text-heading", "mb-3") }) { Text("Branches") }
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                            r.branches.forEach { b ->
                                Card(classes = "p-4") {
                                    P(attrs = { classes("font-semibold", "text-heading") }) { Text(b.name) }
                                    P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("📍 ${b.address}") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
