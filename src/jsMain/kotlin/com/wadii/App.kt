package com.wadii

import androidx.compose.runtime.Composable
import com.wadii.pages.admin.AdminDashboardPage
import com.wadii.pages.admin.AdsPage
import com.wadii.pages.admin.ProviderRequestsPage
import com.wadii.pages.admin.ServicesPage
import com.wadii.pages.auth.LoginPage
import com.wadii.pages.auth.RegisterPage
import com.wadii.pages.provider.ProviderDashboardPage
import com.wadii.pages.provider.ProviderOffersPage
import com.wadii.pages.provider.ProviderOrdersPage
import com.wadii.pages.provider.RespondToOrderPage
import com.wadii.pages.shared.ChatPage
import com.wadii.pages.shared.NotificationsPage
import com.wadii.pages.shared.ProfilePage
import com.wadii.pages.user.*
import com.wadii.router.Route
import com.wadii.state.AppState
import com.wadii.ui.Layout
import com.wadii.ui.Toast
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable

fun main() {
    // AbortError is expected when Ktor cancels in-flight fetch requests on coroutine cancellation
    window.addEventListener("unhandledrejection", { event ->
        if (event.asDynamic().reason?.name == "AbortError") event.preventDefault()
    })
    renderComposable(rootElementId = "root") {
        App()
    }
}

@Composable
fun App() {
    val route = AppState.route

    // Auth screens — no layout
    if (route == Route.LOGIN) { LoginPage(); Toast(); return }
    if (route == Route.REGISTER) { RegisterPage(); Toast(); return }

    // All other screens use the sidebar layout
    Layout {
        when (route) {
            // User
            Route.HOME -> HomePage()
            Route.SEARCH -> SearchPage()
            Route.ORDERS -> OrdersPage()
            Route.NEW_ORDER -> NewOrderPage()
            Route.ORDER_DETAIL -> OrderDetailPage()
            Route.SAVED_OFFERS -> SavedOffersPage()
            Route.PROVIDER_DETAIL -> ProviderDetailPage()

            // Provider
            Route.PROVIDER_DASHBOARD -> ProviderDashboardPage()
            Route.PROVIDER_ORDERS -> ProviderOrdersPage()
            Route.RESPOND_ORDER -> RespondToOrderPage()
            Route.PROVIDER_OFFERS -> ProviderOffersPage()

            // Admin
            Route.ADMIN_DASHBOARD -> AdminDashboardPage()
            Route.PROVIDER_REQUESTS -> ProviderRequestsPage()
            Route.ADVERTISEMENTS -> AdsPage()
            Route.SERVICES -> ServicesPage()

            // Shared
            Route.CHAT -> ChatPage()
            Route.NOTIFICATIONS -> NotificationsPage()
            Route.PROFILE -> ProfilePage()

            else -> Div {}
        }
    }

    Toast()
}
