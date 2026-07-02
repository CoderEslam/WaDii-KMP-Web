package com.wadii.screens.auth.login

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.screens.auth.register.RegisterScreen
import com.wadii.state.AppState
import com.wadii.ui.InputField
import com.wadii.ui.PrimaryButton
import org.jetbrains.compose.web.dom.*

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<LoginViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("min-h-screen", "flex", "items-center", "justify-center", "p-4") }) {
            Div(attrs = { classes("w-full", "max-w-md") }) {
                Div(attrs = { classes("text-center", "mb-10") }) {
                    H1(attrs = { classes("text-5xl", "font-extrabold", "brand-text", "tracking-tight", "mb-3") }) { Text("WaDii") }
                    P(attrs = { classes("text-body-subtle", "text-sm", "tracking-widest", "uppercase") }) { Text("Auto spare parts marketplace") }
                }

                Div(attrs = { classes("bg-surface", "rounded-neu-base", "shadow-neu-lg", "p-8", "border", "border-default") }) {
                    H2(attrs = { classes("text-xl", "font-semibold", "text-heading", "mb-6") }) { Text("Sign In") }
                    Div(attrs = { classes("space-y-5") }) {
                        InputField("Email", state.email, "you@example.com", "email", true) { model.onEvent(LoginEvent.SetEmail(it)) }
                        InputField("Password", state.password, "••••••••", "password", true) { model.onEvent(LoginEvent.SetPassword(it)) }

                        PrimaryButton("Sign In", loading = state.loading, fullWidth = true) {
                            model.onEvent(LoginEvent.Submit)
                        }
                    }

                    P(attrs = { classes("mt-6", "text-center", "text-sm", "text-body-subtle") }) {
                        Text("No account yet? ")
                        Span(attrs = {
                            classes("text-fg-brand", "font-semibold", "cursor-pointer", "hover:underline")
                            onClick { navigator.push(RegisterScreen()) }
                        }) { Text("Create one →") }
                    }
                }

                Div(attrs = { classes("flex", "justify-center", "mt-6") }) {
                    Button(attrs = {
                        classes("text-xs", "text-body-subtle", "hover:text-heading", "flex", "items-center", "gap-2", "transition-colors")
                        onClick { AppState.toggleDarkMode() }
                    }) {
                        val dm = AppState.darkMode
                        Text(if (dm) "☀️ Light mode" else "🌙 Dark mode")
                    }
                }
            }
        }
    }
}
