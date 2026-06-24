package com.wadii

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.wadii.navigation.CurrentScreen
import com.wadii.navigation.Navigator
import com.wadii.screens.*
import com.wadii.state.AppState
import com.wadii.ui.Layout
import com.wadii.ui.Toast
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.renderComposable

fun main() {
    window.addEventListener("unhandledrejection", { event ->
        if (event.asDynamic().reason?.name == "AbortError") event.preventDefault()
    })
    renderComposable(rootElementId = "root") {
        App()
    }
}

@Composable
fun App() {
    // Sync dark mode class to <html> element on every state change
    val darkMode = AppState.darkMode
    SideEffect {
        val cl = document.documentElement?.classList ?: return@SideEffect
        if (darkMode) cl.add("dark") else cl.remove("dark")
    }

    val user = AppState.user

    if (user == null) {
        Navigator(LoginScreen) { CurrentScreen() }
        Toast()
        return
    }

    val initialScreen = when (user.role) {
        "ADMIN" -> AdminDashboardScreen
        "PROVIDER" -> ProviderDashboardScreen
        else -> HomeScreen
    }

    Navigator(initialScreen) {
        Layout { CurrentScreen() }
    }
    Toast()
}
