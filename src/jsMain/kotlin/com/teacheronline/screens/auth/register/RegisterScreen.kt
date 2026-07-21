package com.teacheronline.screens.auth.register

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Card
import com.teacheronline.ui.InputField
import com.teacheronline.ui.PrimaryButton
import org.jetbrains.compose.web.dom.*

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<RegisterViewModel>()
        val s by model.state.collectAsState()

        Div(attrs = { classes("min-h-screen", "flex", "items-center", "justify-center", "p-4", "py-10") }) {
            Div(attrs = { classes("w-full", "max-w-md") }) {
                Div(attrs = { classes("text-center", "mb-10") }) {
                    H1(attrs = { classes("text-5xl", "font-extrabold", "brand-text", "tracking-tight", "mb-3") }) { Text("TeacherOnline") }
                    P(attrs = { classes("text-body-subtle", "text-sm", "tracking-widest", "uppercase") }) { Text("Register your educational center") }
                }

                Card(classes = "p-8") {
                    Div(attrs = { classes("space-y-4") }) {
                        Alert(
                            variant = AlertVariant.Brand,
                            body = "This creates your owner account. Add teachers, students and secretaries afterward from the dashboard."
                        )

                        Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                            InputField("First Name", s.firstName, "Jane", required = true) { model.onEvent(RegisterEvent.SetFirstName(it)) }
                            InputField("Last Name", s.lastName, "Doe", required = true) { model.onEvent(RegisterEvent.SetLastName(it)) }
                        }

                        InputField("Email", s.email, "you@example.com", "email", true) { model.onEvent(RegisterEvent.SetEmail(it)) }
                        InputField("Password", s.password, "••••••••", "password", true) { model.onEvent(RegisterEvent.SetPassword(it)) }

                        PrimaryButton("Create Account", loading = s.loading, fullWidth = true) {
                            model.onEvent(RegisterEvent.Submit)
                        }
                    }

                    P(attrs = { classes("mt-6", "text-center", "text-sm", "text-body-subtle") }) {
                        Text("Already have an account? ")
                        Span(attrs = {
                            classes("text-fg-brand", "font-semibold", "cursor-pointer", "hover:underline")
                            onClick { navigator.pop() }
                        }) { Text("Sign in →") }
                    }
                }
            }
        }
    }
}
