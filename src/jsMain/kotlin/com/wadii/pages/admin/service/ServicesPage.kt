package com.wadii.pages.admin.service

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.ui.InputField
import com.wadii.ui.LoadingScreen
import com.wadii.ui.Spinner
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

class ServicesScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<ServicesViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Services") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) { Text(state.error!!) }
                else -> {
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-5", "shadow-sm") }) {
                        H2(attrs = { classes("font-semibold", "text-slate-800", "mb-3") }) { Text("Add Service") }
                        Div(attrs = { classes("flex", "gap-3") }) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField("Service name", state.newName, required = true) { model.onEvent(ServicesEvent.SetNewName(it)) }
                            }
                            Button(attrs = {
                                classes("px-5", "py-2.5", "bg-amber-500", "text-white", "font-medium", "rounded-xl", "text-sm", "hover:bg-amber-600", "disabled:opacity-60", "flex", "items-center", "gap-2", "self-end")
                                attr("type", "button")
                                onClick { model.onEvent(ServicesEvent.Add) }
                                if (state.adding) disabled()
                            }) { if (state.adding) Spinner() else Text("Add") }
                        }
                    }

                    if (state.services.isEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                            P(attrs = { classes("text-5xl", "mb-3") }) { Text("🔧") }
                            P(attrs = { classes("text-slate-500") }) { Text("No services yet.") }
                        }
                    } else {
                        Div(attrs = { classes("bg-white", "rounded-2xl", "shadow-sm", "divide-y", "divide-slate-100") }) {
                            state.services.forEach { service ->
                                Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                    if (state.editingId == service.id) {
                                        Div(attrs = { classes("flex-1") }) {
                                            InputField("Name", state.editName) { model.onEvent(ServicesEvent.SetEditName(it)) }
                                        }
                                        Button(attrs = {
                                            classes("px-3", "py-1.5", "bg-amber-500", "text-white", "text-sm", "rounded-lg", "hover:bg-amber-600", "disabled:opacity-60", "flex", "items-center", "gap-1")
                                            onClick { model.onEvent(ServicesEvent.SaveEdit(service)) }
                                            if (state.saving) disabled()
                                        }) { if (state.saving) Spinner() else Text("Save") }
                                        Button(attrs = {
                                            classes("px-3", "py-1.5", "border", "border-slate-300", "text-slate-600", "text-sm", "rounded-lg", "hover:bg-slate-50")
                                            onClick { model.onEvent(ServicesEvent.CancelEdit) }
                                        }) { Text("Cancel") }
                                    } else {
                                        Div(attrs = { classes("flex", "items-center", "gap-3", "flex-1") }) {
                                            Div(attrs = { classes("w-8", "h-8", "bg-amber-50", "rounded-lg", "flex", "items-center", "justify-center") }) {
                                                Span(attrs = { classes("text-sm") }) { Text("🔧") }
                                            }
                                            P(attrs = { classes("font-medium", "text-slate-800") }) { Text(service.name) }
                                        }
                                        Button(attrs = {
                                            classes("p-1.5", "text-slate-400", "hover:text-amber-500", "hover:bg-amber-50", "rounded-lg", "transition-colors")
                                            onClick { model.onEvent(ServicesEvent.StartEdit(service)) }
                                        }) { Text("✏️") }
                                        Button(attrs = {
                                            classes("p-1.5", "text-slate-400", "hover:text-red-500", "hover:bg-red-50", "rounded-lg", "transition-colors")
                                            onClick { model.onEvent(ServicesEvent.Delete(service.id)) }
                                        }) { Text("🗑️") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
