package com.wadii.pages.user

import androidx.compose.runtime.*
import com.wadii.api.*
import com.wadii.api.to1dp
import com.wadii.model.Provider
import com.wadii.router.Route
import com.wadii.state.AppState
import com.wadii.ui.LoadingSkeletons
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.*

@Composable
fun ProviderDetailPage() {
    val providerId = AppState.routeParam?.toLongOrNull() ?: return
    var provider by remember { mutableStateOf<Provider?>(null) }
    var loading by remember { mutableStateOf(true) }
    var following by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(providerId) {
        provider = apiGetProvider(providerId)
        loading = false
    }

    Div(attrs = { classes("space-y-6") }) {
        Button(attrs = {
            classes("text-sm", "text-slate-500", "hover:text-slate-700")
            onClick { AppState.navigate(Route.SEARCH) }
        }) { Text("← Back") }

        if (loading) {
            LoadingSkeletons(3)
        } else if (provider == null) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) {
                Text("Provider not found.")
            }
        } else {
            val p = provider!!

            // Header card
            Div(attrs = { classes("bg-white", "rounded-2xl", "overflow-hidden", "shadow-sm") }) {
                Div(attrs = { classes("h-32", "bg-gradient-to-r", "from-amber-400", "to-orange-400") }) {}
                Div(attrs = { classes("px-6", "pb-6") }) {
                    Div(attrs = { classes("flex", "items-end", "justify-between", "-mt-10", "mb-4") }) {
                        Div(attrs = { classes("w-20", "h-20", "rounded-2xl", "bg-white", "border-4",
                            "border-white", "shadow", "flex", "items-center", "justify-center",
                            "text-3xl", "font-bold", "text-amber-600") }) {
                            Text(p.name.firstOrNull()?.toString() ?: "?")
                        }
                        Button(attrs = {
                            classes("flex", "items-center", "gap-2", "px-4", "py-2", "rounded-xl",
                                "text-sm", "font-medium", "transition-colors", "mt-10",
                                if (following) "bg-slate-100 text-slate-700 hover:bg-slate-200"
                                else "bg-amber-500 text-white hover:bg-amber-600")
                            onClick {
                                scope.launch {
                                    if (following) {
                                        if (apiUnfollowProvider(p.id)) { following = false; AppState.toast("Unfollowed") }
                                    } else {
                                        if (apiFollowProvider(p.id)) { following = true; AppState.toast("Following!") }
                                    }
                                }
                            }
                        }) { Text(if (following) "✓ Unfollow" else "+ Follow") }
                    }
                    H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text(p.name) }
                    Div(attrs = { classes("flex", "flex-wrap", "items-center", "gap-4", "mt-2",
                        "text-sm", "text-slate-600") }) {
                        Span { Text("⭐ ${p.rate.to1dp()} rating") }
                        Span { Text("👥 ${p.followersCount} followers") }
                    }
                    p.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                        Div(attrs = { classes("mt-4", "flex", "flex-wrap", "gap-2") }) {
                            svcs.forEach { s ->
                                Span(attrs = { classes("px-3", "py-1", "bg-amber-50", "text-amber-700",
                                    "text-sm", "rounded-full") }) { Text(s.name) }
                            }
                        }
                    }
                }
            }

            // Branches
            p.branches?.takeIf { it.isNotEmpty() }?.let { branches ->
                Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                    H2(attrs = { classes("font-semibold", "text-slate-800", "mb-4") }) { Text("Branches") }
                    Div(attrs = { classes("space-y-4") }) {
                        branches.forEach { branch ->
                            Div(attrs = { classes("border", "border-slate-100", "rounded-xl", "p-4") }) {
                                P(attrs = { classes("font-medium", "text-slate-800") }) { Text(branch.name) }
                                P(attrs = { classes("text-sm", "text-slate-500") }) { Text("📍 ${branch.address}") }
                                branch.workTimes?.takeIf { it.isNotEmpty() }?.let { wts ->
                                    Div(attrs = { classes("mt-2", "space-y-1") }) {
                                        wts.forEach { wt ->
                                            P(attrs = { classes("text-xs", "text-slate-500") }) {
                                                Text("🕐 ${wt.day}: ${wt.startTime} – ${wt.closeTime}")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Links
            p.links?.takeIf { it.isNotEmpty() }?.let { links ->
                Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
                    H2(attrs = { classes("font-semibold", "text-slate-800", "mb-3") }) { Text("Links") }
                    Div(attrs = { classes("space-y-2") }) {
                        links.forEach { l ->
                            A(href = l.link, attrs = {
                                classes("block", "text-sm", "text-amber-600", "hover:underline", "truncate")
                                attr("target", "_blank")
                                attr("rel", "noopener noreferrer")
                            }) { Text(l.link) }
                        }
                    }
                }
            }

            // CTA
            Div(attrs = { classes("bg-gradient-to-r", "from-amber-500", "to-orange-500",
                "rounded-2xl", "p-6", "text-white") }) {
                H2(attrs = { classes("text-lg", "font-semibold", "mb-2") }) { Text("Need a service?") }
                P(attrs = { classes("text-amber-100", "text-sm", "mb-4") }) {
                    Text("Create an order and providers will respond with a quote.")
                }
                Button(attrs = {
                    classes("bg-white", "text-amber-600", "font-semibold", "px-5", "py-2",
                        "rounded-lg", "hover:bg-amber-50", "transition-colors", "text-sm")
                    onClick { AppState.navigate(Route.NEW_ORDER) }
                }) { Text("Create Order") }
            }
        }
    }
}
