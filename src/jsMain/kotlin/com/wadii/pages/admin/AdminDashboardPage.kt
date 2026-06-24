package com.wadii.pages.admin

import androidx.compose.runtime.*
import com.wadii.api.apiGetAllAds
import com.wadii.api.apiGetAllRequests
import com.wadii.api.apiGetAllServices
import com.wadii.router.Route
import com.wadii.state.AppState
import org.jetbrains.compose.web.dom.*

@Composable
fun AdminDashboardPage() {
    var requestCount by remember { mutableStateOf(0) }
    var adCount by remember { mutableStateOf(0) }
    var serviceCount by remember { mutableStateOf(0) }
    var recentRequests by remember { mutableStateOf(listOf<com.wadii.model.ProviderRequest>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val requests = apiGetAllRequests()
        val ads = apiGetAllAds()
        val services = apiGetAllServices()
        requestCount = requests.size
        adCount = ads.size
        serviceCount = services.size
        recentRequests = requests.take(5)
        loading = false
    }

    Div(attrs = { classes("space-y-8") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Admin Dashboard") }

        if (loading) {
            Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-3", "gap-4") }) {
                repeat(3) { Div(attrs = { classes("h-28", "bg-white", "rounded-2xl", "animate-pulse") }) {} }
            }
        } else {
            Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-3", "gap-4") }) {
                StatButton("Provider Requests", requestCount, "📋", Route.PROVIDER_REQUESTS)
                StatButton("Advertisements", adCount, "📣", Route.ADVERTISEMENTS)
                StatButton("Services", serviceCount, "🔧", Route.SERVICES)
            }

            Div {
                Div(attrs = { classes("flex", "items-center", "justify-between", "mb-3") }) {
                    H2(attrs = { classes("font-semibold", "text-slate-800") }) { Text("Pending Provider Requests") }
                    Button(attrs = {
                        classes("text-sm", "text-amber-600", "hover:underline")
                        onClick { AppState.navigate(Route.PROVIDER_REQUESTS) }
                    }) { Text("View all") }
                }
                if (recentRequests.isEmpty()) {
                    Div(attrs = { classes("bg-white", "rounded-xl", "p-8", "text-center",
                        "text-slate-500", "text-sm") }) { Text("No pending requests.") }
                } else {
                    Div(attrs = { classes("space-y-2") }) {
                        recentRequests.forEach { req ->
                            Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl",
                                "p-4", "flex", "items-center", "justify-between") }) {
                                Div {
                                    P(attrs = { classes("font-medium", "text-slate-800") }) { Text(req.name) }
                                    P(attrs = { classes("text-sm", "text-slate-500") }) {
                                        Text("📞 ${req.phoneNumber} · 📍 ${req.address}")
                                    }
                                }
                                Button(attrs = {
                                    classes("text-sm", "text-amber-600", "font-medium", "hover:underline")
                                    onClick { AppState.navigate(Route.PROVIDER_REQUESTS) }
                                }) { Text("Review →") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatButton(label: String, value: Int, emoji: String, route: Route) {
    Div(attrs = {
        classes("bg-white", "rounded-2xl", "p-6", "shadow-sm", "hover:shadow-md",
            "transition-shadow", "cursor-pointer")
        onClick { AppState.navigate(route) }
    }) {
        Div(attrs = { classes("inline-flex", "p-3", "bg-amber-50", "rounded-xl", "mb-3") }) {
            Span(attrs = { classes("text-xl") }) { Text(emoji) }
        }
        P(attrs = { classes("text-3xl", "font-bold", "text-slate-800") }) { Text(value.toString()) }
        P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(label) }
    }
}
