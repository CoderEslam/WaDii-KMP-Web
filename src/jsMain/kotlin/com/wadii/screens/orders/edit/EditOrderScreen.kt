package com.wadii.screens.orders.edit

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.ui.BackButton
import com.wadii.ui.Card
import org.jetbrains.compose.web.dom.*

class EditOrderScreen(val orderId: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            BackButton("Back to orders") { navigator.pop() }
            Card(classes = "p-8 text-center") {
                P(attrs = { classes("text-4xl", "mb-3") }) { Text("🚧") }
                H1(attrs = { classes("text-lg", "font-semibold", "text-heading", "mb-1") }) { Text("Editing order #$orderId") }
                P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("Order editing is coming soon.") }
            }
        }
    }
}

