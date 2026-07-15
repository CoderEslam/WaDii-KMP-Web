package com.teacheronline.pages.admin.config

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.ui.Alert
import com.teacheronline.ui.AlertVariant
import com.teacheronline.ui.Card
import com.teacheronline.ui.InputField
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.PrimaryButton
import com.teacheronline.ui.SecondaryButton
import org.jetbrains.compose.web.dom.*

class ConfigScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<ConfigViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Configuration") }
            Alert(variant = AlertVariant.Brand, body = "Look up a config key (e.g. attendance_time_range) and change its value.")

            if (state.error != null) Alert(variant = AlertVariant.Danger, body = state.error!!)

            Card(classes = "p-5", ) {
                Div(attrs = { classes("flex", "gap-3", "items-end", "mb-4") }) {
                    Div(attrs = { classes("flex-1") }) {
                        InputField("Key", state.key) { model.onEvent(ConfigEvent.SetKey(it)) }
                    }
                    SecondaryButton("Look up") { model.onEvent(ConfigEvent.Load) }
                }
                if (state.isLoading) {
                    LoadingScreen()
                } else {
                    P(attrs = { classes("text-sm", "text-body-subtle", "mb-3") }) {
                        Text("Current value: ${state.currentValue ?: "—"}")
                    }
                    Div(attrs = { classes("flex", "gap-3", "items-end") }) {
                        Div(attrs = { classes("w-40") }) {
                            InputField("New value", state.newValue, type = "number") { model.onEvent(ConfigEvent.SetNewValue(it)) }
                        }
                        PrimaryButton("Save", loading = state.saving) { model.onEvent(ConfigEvent.Save) }
                    }
                }
            }
        }
    }
}
