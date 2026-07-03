package com.wadii

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.wadii.core.call.CallSignalingController
import com.wadii.core.di.initializeKoin
import com.wadii.pages.admin.dashboard.AdminDashboardScreen
import com.wadii.pages.provider.dashboard.ProviderDashboardScreen
import com.wadii.pages.shared.call.IncomingCallOverlay
import com.wadii.screens.auth.login.LoginScreen
import com.wadii.screens.home.HomeScreen
import com.wadii.state.AppState
import com.wadii.ui.Layout
import com.wadii.ui.Toast
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.renderComposable
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

fun main() {
    initializeKoin()
    window.addEventListener("unhandledrejection", { event ->
        if (event.asDynamic().reason?.name == "AbortError") event.preventDefault()
    })
    renderComposable(rootElementId = "root") {
       KoinContext {
            App()
        }
    }
}

@Composable
fun App() {
    val darkMode = AppState.darkMode
    SideEffect {
        val cl = document.documentElement?.classList ?: return@SideEffect
        if (darkMode) cl.add("dark") else cl.remove("dark")
    }

    val user = AppState.user
    val token = AppState.token
    val callController = koinInject<CallSignalingController>()
    LaunchedEffect(user?.id, token) {
        if (user != null && token != null) callController.start(user.id, token) else callController.stop()
    }

    if (user == null) {
        Navigator(LoginScreen()) { CurrentScreen() }
        Toast()
        return
    }

    val initialScreen = when (user.role) {
        "ADMIN" -> AdminDashboardScreen()
        "PROVIDER" -> ProviderDashboardScreen()
        else -> HomeScreen()
    }

    Navigator(initialScreen) {
        Layout { CurrentScreen() }
        IncomingCallOverlay()
    }
    Toast()
}
