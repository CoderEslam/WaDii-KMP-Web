package com.wadii.screens.auth.login

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.screens.auth.register.RegisterScreen
import com.wadii.state.AppState
import com.wadii.ui.InputField
import com.wadii.ui.Spinner
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = rememberScreenModel { LoginScreenModel() }
        val state = model.state

        Div(attrs = { classes("min-h-screen", "flex", "items-center", "justify-center", "p-4") }) {
            Div(attrs = { classes("w-full", "max-w-md") }) {
                Div(attrs = { classes("text-center", "mb-10") }) {
                    H1(attrs = { classes("text-5xl", "font-extrabold", "brand-text", "tracking-tight", "mb-3") }) { Text("WaDii") }
                    P(attrs = { classes("text-slate-500", "text-sm", "tracking-widest", "uppercase") }) { Text("Your service universe") }
                }

                Div(attrs = { classes("bg-white", "rounded-3xl", "shadow-lg", "p-8", "border", "border-slate-200") }) {
                    H2(attrs = { classes("text-xl", "font-bold", "text-slate-800", "mb-6") }) { Text("Sign In") }
                    Div(attrs = { classes("space-y-5") }) {
                        InputField("Email", state.email, "you@example.com", "email", true) { model.onEvent(LoginEvent.SetEmail(it)) }
                        InputField("Password", state.password, "••••••••", "password", true) { model.onEvent(LoginEvent.SetPassword(it)) }

                        Button(attrs = {
                            classes("w-full", "py-3", "bg-amber-500", "hover:bg-amber-600", "text-white", "font-bold", "rounded-xl", "transition-all", "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2", "tracking-wide")
                            attr("type", "button")
                            onClick { model.onEvent(LoginEvent.Submit) }
                            if (state.loading) disabled()
                        }) {
                            if (state.loading) Spinner() else Text("Sign In")
                        }
                    }

                    P(attrs = { classes("mt-6", "text-center", "text-sm", "text-slate-500") }) {
                        Text("No account yet? ")
                        Span(attrs = {
                            classes("text-amber-500", "font-semibold", "cursor-pointer", "hover:underline")
                            onClick { navigator.push(RegisterScreen()) }
                        }) { Text("Create one →") }
                    }
                }

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
}
