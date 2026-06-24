package com.wadii.pages.auth

import androidx.compose.runtime.*
import com.wadii.api.apiLogin
import com.wadii.screens.RegisterScreen
import com.wadii.state.AppState
import com.wadii.ui.InputField
import com.wadii.ui.Spinner
import com.wadii.navigation.LocalNavigator
import com.wadii.navigation.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun LoginPage() {
    val navigator = LocalNavigator.currentOrThrow
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        if (loading) return
        loading = true
        scope.launch {
            val user = apiLogin(email, password)
            loading = false
            if (user != null && user.token != null) {
                AppState.login(user, user.token!!)
                AppState.toast("Welcome back, ${user.firstName}!")
            } else {
                AppState.toast("Invalid email or password", true)
            }
        }
    }

    Div(attrs = { classes("min-h-screen", "flex", "items-center", "justify-center", "p-4") }) {
        Div(attrs = { classes("w-full", "max-w-md") }) {
            // Brand + tagline
            Div(attrs = { classes("text-center", "mb-10") }) {
                H1(attrs = { classes("text-5xl", "font-extrabold", "brand-text", "tracking-tight", "mb-3") }) { Text("WaDii") }
                P(attrs = { classes("text-slate-500", "text-sm", "tracking-widest", "uppercase") }) { Text("Your service universe") }
            }

            Div(attrs = { classes("bg-white", "rounded-3xl", "shadow-lg", "p-8", "border", "border-slate-200") }) {
                H2(attrs = { classes("text-xl", "font-bold", "text-slate-800", "mb-6") }) { Text("Sign In") }
                Div(attrs = { classes("space-y-5") }) {
                    InputField("Email", email, "you@example.com", "email", true) { email = it }
                    InputField("Password", password, "••••••••", "password", true) { password = it }

                    Button(attrs = {
                        classes("w-full", "py-3", "bg-amber-500", "hover:bg-amber-600",
                            "text-white", "font-bold", "rounded-xl", "transition-all",
                            "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2",
                            "tracking-wide")
                        attr("type", "button")
                        onClick { submit() }
                        if (loading) disabled()
                    }) {
                        if (loading) Spinner() else Text("Sign In")
                    }
                }

                P(attrs = { classes("mt-6", "text-center", "text-sm", "text-slate-500") }) {
                    Text("No account yet? ")
                    Span(attrs = {
                        classes("text-amber-500", "font-semibold", "cursor-pointer", "hover:underline")
                        onClick { navigator.push(RegisterScreen) }
                    }) { Text("Create one →") }
                }
            }

            // Dark mode toggle on auth page
            Div(attrs = { classes("flex", "justify-center", "mt-6") }) {
                Button(attrs = {
                    classes("text-xs", "text-slate-400", "hover:text-slate-600", "flex", "items-center", "gap-2", "transition-colors")
                    onClick { AppState.toggleDarkMode() }
                }) {
                    val dm = AppState.darkMode
                    Text(if (dm) "☀️ Light mode" else "🌙 Dark mode")
                }
            }
        }
    }
}
