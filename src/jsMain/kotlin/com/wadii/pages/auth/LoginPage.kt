package com.wadii.pages.auth

import androidx.compose.runtime.*
import com.wadii.api.apiLogin
import com.wadii.state.AppState
import com.wadii.ui.InputField
import com.wadii.ui.Spinner
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun LoginPage() {
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

    Div(attrs = { classes("min-h-screen", "flex", "items-center", "justify-center",
        "bg-gradient-to-br", "from-amber-50", "to-slate-100", "p-4") }) {
        Div(attrs = { classes("w-full", "max-w-md") }) {
            Div(attrs = { classes("text-center", "mb-8") }) {
                H1(attrs = { classes("text-4xl", "font-bold", "text-amber-500") }) { Text("WaDii") }
                P(attrs = { classes("text-slate-600", "mt-2") }) { Text("Sign in to your account") }
            }

            Div(attrs = { classes("bg-white", "rounded-2xl", "shadow-lg", "p-8") }) {
                Div(attrs = { classes("space-y-5") }) {
                    InputField("Email", email, "you@example.com", "email", true) { email = it }
                    InputField("Password", password, "••••••••", "password", true) { password = it }

                    Button(attrs = {
                        classes("w-full", "py-3", "bg-amber-500", "hover:bg-amber-600",
                            "text-white", "font-semibold", "rounded-xl", "transition-colors",
                            "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2")
                        attr("type", "button")
                        onClick { submit() }
                        if (loading) disabled()
                    }) {
                        if (loading) Spinner() else Text("Sign In")
                    }
                }

                P(attrs = { classes("mt-6", "text-center", "text-sm", "text-slate-600") }) {
                    Text("Don't have an account? ")
                    Span(attrs = {
                        classes("text-amber-600", "font-medium", "cursor-pointer", "hover:underline")
                        onClick { AppState.navigate(com.wadii.router.Route.REGISTER) }
                    }) { Text("Register") }
                }
            }
        }
    }
}
