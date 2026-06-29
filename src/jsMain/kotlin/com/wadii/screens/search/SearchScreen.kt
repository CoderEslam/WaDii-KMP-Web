package com.wadii.screens.search

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.data.api.to1dp
import com.wadii.screens.home.OfferCard
import com.wadii.screens.providerDetail.ProviderDetailScreen
import com.wadii.ui.Spinner
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

class SearchScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<SearchViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Search") }

            Div(attrs = { classes("flex", "gap-3") }) {
                Div(attrs = { classes("flex-1", "relative") }) {
                    Span(attrs = { classes("absolute", "left-4", "top-1/2", "-translate-y-1/2", "text-slate-400") }) { Text("🔍") }
                    Input(type = InputType.Text, attrs = {
                        classes("w-full", "pl-10", "pr-4", "py-3", "border", "border-slate-300", "rounded-xl", "focus:outline-none", "focus:ring-2", "focus:ring-amber-400", "bg-white")
                        attr("placeholder", "Search providers, offers, services…")
                        attr("value", state.query)
                        onInput { model.onEvent(SearchEvent.SetQuery(it.value)) }
                        onKeyDown { if (it.key == "Enter") model.onEvent(SearchEvent.Search) }
                    })
                }
                Button(attrs = {
                    classes("px-6", "py-3", "bg-amber-500", "hover:bg-amber-600", "text-white", "font-semibold", "rounded-xl", "transition-colors", "disabled:opacity-60", "flex", "items-center", "gap-2")
                    onClick { model.onEvent(SearchEvent.Search) }
                    if (state.searching) disabled()
                }) { if (state.searching) Spinner() else Text("Search") }
            }

            if (state.searching) {
                Div(attrs = { classes("flex", "justify-center", "py-12") }) { Spinner() }
            }

            state.results?.let { r ->
                val total = r.providers.size + r.offers.size + r.services.size + r.branches.size
                if (total == 0) {
                    Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) {
                        Text("No results found for \"${state.query}\"")
                    }
                }
                if (r.providers.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-base", "font-semibold", "text-slate-700", "mb-3") }) { Text("Providers") }
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                            r.providers.forEach { p ->
                                Div(attrs = {
                                    classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-4", "hover:shadow-md", "transition-shadow", "flex", "items-center", "gap-4", "cursor-pointer")
                                    onClick { navigator.push(ProviderDetailScreen(p.id)) }
                                }) {
                                    Div(attrs = { classes("w-12", "h-12", "rounded-full", "bg-amber-100", "flex", "items-center", "justify-center", "text-amber-700", "font-bold", "text-lg", "flex-shrink-0") }) {
                                        Text(p.name.firstOrNull()?.toString() ?: "?")
                                    }
                                    Div(attrs = { classes("flex-1") }) {
                                        P(attrs = { classes("font-semibold", "text-slate-800") }) { Text(p.name) }
                                        P(attrs = { classes("text-sm", "text-slate-500") }) { Text("⭐ ${p.rate.to1dp()} · ${p.followersCount} followers") }
                                    }
                                }
                            }
                        }
                    }
                }
                if (r.offers.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-base", "font-semibold", "text-slate-700", "mb-3") }) { Text("Offers") }
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                            r.offers.forEach { OfferCard(navigator = navigator, offer = it) }
                        }
                    }
                }
                if (r.services.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-base", "font-semibold", "text-slate-700", "mb-3") }) { Text("Services") }
                        Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                            r.services.forEach { svc ->
                                Span(attrs = { classes("bg-white", "border", "border-slate-200", "px-4", "py-2", "rounded-full", "text-sm", "text-slate-700") }) { Text(svc.name) }
                            }
                        }
                    }
                }
                if (r.branches.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-base", "font-semibold", "text-slate-700", "mb-3") }) { Text("Branches") }
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                            r.branches.forEach { b ->
                                Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-4") }) {
                                    P(attrs = { classes("font-semibold", "text-slate-800") }) { Text(b.name) }
                                    P(attrs = { classes("text-sm", "text-slate-500") }) { Text("📍 ${b.address}") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
