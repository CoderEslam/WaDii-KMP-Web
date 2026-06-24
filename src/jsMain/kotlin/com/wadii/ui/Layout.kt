package com.wadii.ui

import androidx.compose.runtime.*
import com.wadii.router.Route
import com.wadii.state.AppState
import org.jetbrains.compose.web.dom.*

private data class NavLink(val label: String, val icon: String, val route: Route)

@Composable
fun Layout(content: @Composable () -> Unit) {
    val user = AppState.user ?: return
    var menuOpen by remember { mutableStateOf(false) }

    val userLinks = listOf(
        NavLink("Home", "🏠", Route.HOME),
        NavLink("Search", "🔍", Route.SEARCH),
        NavLink("My Orders", "📦", Route.ORDERS),
        NavLink("Saved Offers", "⭐", Route.SAVED_OFFERS),
        NavLink("Messages", "💬", Route.CHAT),
        NavLink("Notifications", "🔔", Route.NOTIFICATIONS),
    )
    val providerLinks = listOf(
        NavLink("Dashboard", "📊", Route.PROVIDER_DASHBOARD),
        NavLink("Orders", "📦", Route.PROVIDER_ORDERS),
        NavLink("My Offers", "⭐", Route.PROVIDER_OFFERS),
        NavLink("Messages", "💬", Route.CHAT),
        NavLink("Notifications", "🔔", Route.NOTIFICATIONS),
    )
    val adminLinks = listOf(
        NavLink("Dashboard", "📊", Route.ADMIN_DASHBOARD),
        NavLink("Provider Requests", "📋", Route.PROVIDER_REQUESTS),
        NavLink("Advertisements", "📣", Route.ADVERTISEMENTS),
        NavLink("Services", "🔧", Route.SERVICES),
    )

    val links = when (user.role) {
        "ADMIN" -> adminLinks
        "PROVIDER" -> providerLinks
        else -> userLinks
    }

    Div(attrs = { classes("flex", "min-h-screen", "bg-slate-50") }) {
        // Desktop Sidebar
        Aside(attrs = {
            classes("hidden", "md:flex", "flex-col", "w-64", "bg-white",
                "border-r", "border-slate-200", "fixed", "inset-y-0", "left-0", "z-30")
        }) {
            // Logo
            Div(attrs = { classes("p-6", "border-b", "border-slate-200") }) {
                Span(attrs = {
                    classes("text-2xl", "font-bold", "text-amber-500", "cursor-pointer")
                    onClick { AppState.navigate(Route.HOME) }
                }) { Text("WaDii") }
                P(attrs = { classes("text-xs", "text-slate-500", "mt-1", "capitalize") }) {
                    Text(user.role.lowercase())
                }
            }

            // Nav
            Nav(attrs = { classes("flex-1", "p-4", "space-y-1", "overflow-y-auto") }) {
                links.forEach { link -> SidebarLink(link) }
            }

            // Bottom
            Div(attrs = { classes("p-4", "border-t", "border-slate-200", "space-y-1") }) {
                SidebarLink(NavLink("Profile", "👤", Route.PROFILE))
                Button(attrs = {
                    classes("w-full", "flex", "items-center", "gap-3", "px-3", "py-2",
                        "rounded-lg", "text-sm", "text-red-600", "hover:bg-red-50",
                        "transition-colors", "text-left")
                    onClick { AppState.logout() }
                }) {
                    Span { Text("🚪") }
                    Span { Text("Logout") }
                }
            }
        }

        // Mobile header
        Header(attrs = {
            classes("md:hidden", "fixed", "top-0", "left-0", "right-0", "bg-white",
                "border-b", "border-slate-200", "z-30", "flex", "items-center",
                "justify-between", "px-4", "h-14")
        }) {
            Span(attrs = {
                classes("text-xl", "font-bold", "text-amber-500", "cursor-pointer")
                onClick { AppState.navigate(Route.HOME) }
            }) { Text("WaDii") }
            Button(attrs = {
                classes("p-2", "text-slate-600")
                onClick { menuOpen = !menuOpen }
            }) { Text(if (menuOpen) "✕" else "☰") }
        }

        // Mobile menu overlay
        if (menuOpen) {
            Div(attrs = {
                classes("md:hidden", "fixed", "inset-0", "z-20", "bg-black/50")
                onClick { menuOpen = false }
            }) {
                Div(attrs = {
                    classes("absolute", "left-0", "top-14", "bottom-0", "w-64",
                        "bg-white", "shadow-xl", "p-4", "space-y-1")
                    onClick { it.stopPropagation() }
                }) {
                    links.forEach { link ->
                        SidebarLink(link) { menuOpen = false }
                    }
                    SidebarLink(NavLink("Profile", "👤", Route.PROFILE)) { menuOpen = false }
                    Button(attrs = {
                        classes("w-full", "flex", "items-center", "gap-3", "px-3", "py-2",
                            "rounded-lg", "text-sm", "text-red-600", "hover:bg-red-50")
                        onClick { AppState.logout() }
                    }) {
                        Span { Text("🚪") }
                        Span { Text("Logout") }
                    }
                }
            }
        }

        // Main content
        Main(attrs = {
            classes("flex-1", "md:ml-64", "pt-14", "md:pt-0", "min-h-screen")
        }) {
            Div(attrs = { classes("max-w-5xl", "mx-auto", "p-6") }) {
                content()
            }
        }
    }
}

@Composable
private fun SidebarLink(link: NavLink, afterClick: (() -> Unit)? = null) {
    val active = AppState.route == link.route
    val base = "flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors w-full text-left"
    val colors = if (active) "bg-amber-50 text-amber-700" else "text-slate-600 hover:bg-slate-100"
    Button(attrs = {
        attr("class", "$base $colors")
        onClick {
            AppState.navigate(link.route)
            afterClick?.invoke()
        }
    }) {
        Span { Text(link.icon) }
        Span { Text(link.label) }
    }
}
