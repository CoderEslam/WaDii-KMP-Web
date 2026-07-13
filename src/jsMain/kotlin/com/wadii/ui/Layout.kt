package com.wadii.ui

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.pages.admin.ads.AdsScreen
import com.wadii.pages.admin.dashboard.AdminDashboardScreen
import com.wadii.pages.admin.provider.ProviderRequestsScreen
import com.wadii.pages.admin.reason.ReasonsScreen
import com.wadii.pages.admin.service.ServicesScreen
import com.wadii.pages.provider.dashboard.ProviderDashboardScreen
import com.wadii.pages.provider.offers.ProviderOffersScreen
import com.wadii.pages.provider.orders.ProviderOrdersScreen
import com.wadii.pages.shared.chat.ChatScreen
import com.wadii.pages.shared.notifications.NotificationsScreen
import com.wadii.pages.shared.profile.ProfileScreen
import com.wadii.screens.home.HomeScreen
import com.wadii.screens.orders.list.OrdersScreen
import com.wadii.screens.savedOffers.SavedOffersScreen
import com.wadii.screens.search.SearchScreen
import com.wadii.state.AppState
import org.jetbrains.compose.web.dom.*

private data class NavLink(val label: String, val icon: String, val screen: Screen)

@Composable
fun Layout(content: @Composable () -> Unit) {
    val user = AppState.user ?: return
    val navigator = LocalNavigator.currentOrThrow
    val darkMode = AppState.darkMode
    var menuOpen by remember { mutableStateOf(false) }

    val userLinks = listOf(
        NavLink("Home", "✦", HomeScreen()),
        NavLink("Search", "⊹", SearchScreen()),
        NavLink("My Orders", "◈", OrdersScreen()),
        NavLink("Saved Deals", "◉", SavedOffersScreen()),
        NavLink("Messages", "◎", ChatScreen()),
        NavLink("Notifications", "◇", NotificationsScreen()),
    )
    val providerLinks = listOf(
        NavLink("Dashboard", "⬡", ProviderDashboardScreen()),
        NavLink("Orders", "◈", ProviderOrdersScreen()),
        NavLink("My Deals", "◉", ProviderOffersScreen()),
        NavLink("Messages", "◎", ChatScreen()),
        NavLink("Notifications", "◇", NotificationsScreen()),
    )
    val adminLinks = listOf(
        NavLink("Dashboard", "⬡", AdminDashboardScreen()),
        NavLink("Seller Requests", "◈", ProviderRequestsScreen()),
        NavLink("Ads", "◉", AdsScreen()),
        NavLink("Categories", "⊹", ServicesScreen()),
        NavLink("Reasons", "🏷", ReasonsScreen()),
    )

    val links = when (user.role) {
        "ADMIN" -> adminLinks
        "PROVIDER" -> providerLinks
        else -> userLinks
    }

    Div(attrs = { classes("flex", "min-h-screen") }) {
        // Desktop Sidebar
        Aside(attrs = {
            classes("hidden", "md:flex", "flex-col", "w-64", "fixed", "inset-y-0", "left-0", "z-30", "space-sidebar")
        }) {
            Div(attrs = { classes("px-6", "py-5", "border-b", "border-default") }) {
                Span(attrs = {
                    classes("text-2xl", "font-extrabold", "cursor-pointer", "brand-text", "tracking-tight")
                    onClick { navigator.replaceAll(links.first().screen) }
                }) { Text("WaDii") }
                P(attrs = { classes("text-xs", "text-body-subtle", "mt-0.5", "tracking-widest", "uppercase") }) {
                    Text(user.role.lowercase())
                }
            }

            Nav(attrs = { classes("flex-1", "px-3", "py-4", "space-y-0.5", "overflow-y-auto") }) {
                links.forEach { link -> SpaceNavLink(link, navigator) }
            }

            Div(attrs = { classes("px-3", "py-4", "border-t", "border-default", "space-y-0.5") }) {
                SpaceNavLink(NavLink("Profile", "◑", ProfileScreen()), navigator)

                Button(attrs = {
                    classes("dark-toggle")
                    onClick { AppState.toggleDarkMode() }
                }) {
                    Span(attrs = { classes("text-base", "w-5", "text-center") }) { Text(if (darkMode) "☀️" else "🌙") }
                    Span { Text(if (darkMode) "Light Mode" else "Dark Mode") }
                }

                Button(attrs = {
                    classes("dark-toggle", "text-fg-danger")
                    onClick { AppState.logout() }
                }) {
                    Span(attrs = { classes("text-base", "w-5", "text-center") }) { Text("→") }
                    Span { Text("Sign Out") }
                }
            }
        }

        // Mobile header
        Header(attrs = {
            classes("md:hidden", "fixed", "top-0", "left-0", "right-0", "z-30", "space-header",
                "flex", "items-center", "justify-between", "px-4", "h-14")
        }) {
            Span(attrs = {
                classes("text-xl", "font-extrabold", "cursor-pointer", "brand-text", "tracking-tight")
                onClick { navigator.replaceAll(links.first().screen) }
            }) { Text("WaDii") }
            Div(attrs = { classes("flex", "items-center", "gap-2") }) {
                Button(attrs = {
                    classes("p-2", "text-body-subtle", "hover:text-fg-brand", "transition-colors", "text-lg")
                    onClick { AppState.toggleDarkMode() }
                }) { Text(if (darkMode) "☀️" else "🌙") }
                Button(attrs = {
                    classes("p-2", "text-body", "text-xl")
                    onClick { menuOpen = !menuOpen }
                }) { Text(if (menuOpen) "✕" else "☰") }
            }
        }

        // Mobile menu overlay
        if (menuOpen) {
            Div(attrs = {
                classes("md:hidden", "fixed", "inset-0", "z-20", "bg-black/50")
                onClick { menuOpen = false }
            }) {
                Div(attrs = {
                    classes("absolute", "left-0", "top-14", "bottom-0", "w-64", "space-sidebar", "px-3", "py-4", "space-y-0.5", "overflow-y-auto")
                    onClick { it.stopPropagation() }
                }) {
                    links.forEach { link ->
                        SpaceNavLink(link, navigator) { menuOpen = false }
                    }
                    SpaceNavLink(NavLink("Profile", "◑", ProfileScreen()), navigator) { menuOpen = false }
                    Button(attrs = {
                        classes("dark-toggle", "text-fg-danger")
                        onClick { AppState.logout(); menuOpen = false }
                    }) {
                        Span(attrs = { classes("text-base", "w-5", "text-center") }) { Text("→") }
                        Span { Text("Sign Out") }
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
private fun SpaceNavLink(link: NavLink, navigator: Navigator, afterClick: (() -> Unit)? = null) {
    val active = navigator.lastItem::class == link.screen::class
    Button(attrs = {
        if (active) {
            attr("class", "w-full flex items-center gap-3 px-3 py-2.5 rounded-neu-base text-sm font-semibold transition-all text-left bg-surface shadow-neu-inset text-fg-brand")
        } else {
            attr("class", "w-full flex items-center gap-3 px-3 py-2.5 rounded-neu-base text-sm font-medium transition-all text-left bg-surface text-body hover:shadow-neu-sm hover:text-heading")
        }
        onClick {
            navigator.replaceAll(link.screen)
            afterClick?.invoke()
        }
    }) {
        Span(attrs = {
            if (active) attr("class", "text-base w-5 text-center text-fg-brand")
            else attr("class", "text-base w-5 text-center text-body-subtle")
        }) { Text(link.icon) }
        Span { Text(link.label) }
        if (active) {
            Span(attrs = { classes("ml-auto", "w-1.5", "h-1.5", "rounded-full", "bg-brand") }) {}
        }
    }
}
