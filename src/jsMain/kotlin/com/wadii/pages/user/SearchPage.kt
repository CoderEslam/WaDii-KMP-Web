package com.wadii.pages.user

import androidx.compose.runtime.*
import com.wadii.api.apiSearch
import com.wadii.model.SearchResult
import com.wadii.router.Route
import com.wadii.state.AppState
import com.wadii.api.to1dp
import com.wadii.ui.PrimaryButton
import com.wadii.ui.Spinner
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun SearchPage() {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<SearchResult?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Div(attrs = { classes("space-y-6") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Search") }

        // Search bar
        Div(attrs = { classes("flex", "gap-3") }) {
            Div(attrs = { classes("flex-1", "relative") }) {
                Span(attrs = { classes("absolute", "left-4", "top-1/2", "-translate-y-1/2", "text-slate-400") }) {
                    Text("🔍")
                }
                Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
                    classes("w-full", "pl-10", "pr-4", "py-3", "border", "border-slate-300",
                        "rounded-xl", "focus:outline-none", "focus:ring-2", "focus:ring-amber-400", "bg-white")
                    attr("placeholder", "Search providers, offers, services…")
                    attr("value", query)
                    onInput { query = it.value }
                    onKeyDown { if (it.key == "Enter") {
                        loading = true
                        scope.launch {
                            results = apiSearch(query)
                            loading = false
                        }
                    }}
                })
            }
            Button(attrs = {
                classes("px-6", "py-3", "bg-amber-500", "hover:bg-amber-600", "text-white",
                    "font-semibold", "rounded-xl", "transition-colors", "disabled:opacity-60",
                    "flex", "items-center", "gap-2")
                onClick {
                    if (query.isBlank()) return@onClick
                    loading = true
                    scope.launch {
                        results = apiSearch(query)
                        loading = false
                    }
                }
                if (loading) disabled()
            }) { if (loading) Spinner() else Text("Search") }
        }

        if (loading) {
            Div(attrs = { classes("flex", "justify-center", "py-12") }) { Spinner() }
        }

        results?.let { r ->
            val total = r.providers.size + r.offers.size + r.services.size + r.branches.size
            if (total == 0) {
                Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) {
                    Text("No results found for \"$query\"")
                }
            }

            // Providers
            if (r.providers.isNotEmpty()) {
                Div {
                    P(attrs = { classes("text-base", "font-semibold", "text-slate-700", "mb-3") }) { Text("Providers") }
                    Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                        r.providers.forEach { p ->
                            Div(attrs = {
                                classes("bg-white", "border", "border-slate-200", "rounded-xl",
                                    "p-4", "hover:shadow-md", "transition-shadow", "flex",
                                    "items-center", "gap-4", "cursor-pointer")
                                onClick { AppState.navigate(Route.PROVIDER_DETAIL, p.id.toString()) }
                            }) {
                                Div(attrs = { classes("w-12", "h-12", "rounded-full", "bg-amber-100",
                                    "flex", "items-center", "justify-center", "text-amber-700",
                                    "font-bold", "text-lg", "flex-shrink-0") }) {
                                    Text(p.name.firstOrNull()?.toString() ?: "?")
                                }
                                Div(attrs = { classes("flex-1") }) {
                                    P(attrs = { classes("font-semibold", "text-slate-800") }) { Text(p.name) }
                                    P(attrs = { classes("text-sm", "text-slate-500") }) {
                                        Text("⭐ ${p.rate.to1dp()} · ${p.followersCount} followers")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Offers
            if (r.offers.isNotEmpty()) {
                Div {
                    P(attrs = { classes("text-base", "font-semibold", "text-slate-700", "mb-3") }) { Text("Offers") }
                    Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                        r.offers.forEach { offer -> OfferCard(offer) }
                    }
                }
            }

            // Services
            if (r.services.isNotEmpty()) {
                Div {
                    P(attrs = { classes("text-base", "font-semibold", "text-slate-700", "mb-3") }) { Text("Services") }
                    Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                        r.services.forEach { s ->
                            Span(attrs = {
                                classes("bg-white", "border", "border-slate-200", "px-4", "py-2",
                                    "rounded-full", "text-sm", "text-slate-700", "cursor-pointer",
                                    "hover:border-amber-400")
                                onClick { AppState.navigate(Route.SEARCH) }
                            }) { Text(s.name) }
                        }
                    }
                }
            }

            // Branches
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
