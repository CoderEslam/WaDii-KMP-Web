package com.wadii.pages.admin

import androidx.compose.runtime.*
import com.wadii.ui.InputField
import com.wadii.ui.LoadingScreen
import com.wadii.ui.Spinner
import com.wadii.viewmodel.ServicesEvent
import com.wadii.viewmodel.ServicesScreenModel
import com.wadii.viewmodel.UiState
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun ServicesPage() {
    val model = rememberScreenModel { ServicesScreenModel() }

    Div(attrs = { classes("space-y-6") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Services") }

        when (val s = model.state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) { Text(s.message) }
            is UiState.Success -> {
                val d = s.data

                Div(attrs = { classes("bg-white", "rounded-2xl", "p-5", "shadow-sm") }) {
                    H2(attrs = { classes("font-semibold", "text-slate-800", "mb-3") }) { Text("Add Service") }
                    Div(attrs = { classes("flex", "gap-3") }) {
                        Div(attrs = { classes("flex-1") }) {
                            InputField("Service name", d.newName, required = true) { model.onEvent(ServicesEvent.SetNewName(it)) }
                        }
                        Button(attrs = {
                            classes("px-5", "py-2.5", "bg-amber-500", "text-white", "font-medium", "rounded-xl", "text-sm", "hover:bg-amber-600", "disabled:opacity-60", "flex", "items-center", "gap-2", "self-end")
                            attr("type", "button")
                            onClick { model.onEvent(ServicesEvent.Add) }
                            if (d.adding) disabled()
                        }) { if (d.adding) Spinner() else Text("Add") }
                    }
                }

                if (d.services.isEmpty()) {
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                        P(attrs = { classes("text-5xl", "mb-3") }) { Text("🔧") }
                        P(attrs = { classes("text-slate-500") }) { Text("No services yet.") }
                    }
                } else {
                    Div(attrs = { classes("bg-white", "rounded-2xl", "shadow-sm", "divide-y", "divide-slate-100") }) {
                        d.services.forEach { service ->
                            Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                                if (d.editingId == service.id) {
                                    Div(attrs = { classes("flex-1") }) {
                                        InputField("Name", d.editName) { model.onEvent(ServicesEvent.SetEditName(it)) }
                                    }
                                    Button(attrs = {
                                        classes("px-3", "py-1.5", "bg-amber-500", "text-white", "text-sm", "rounded-lg", "hover:bg-amber-600", "disabled:opacity-60", "flex", "items-center", "gap-1")
                                        onClick { model.onEvent(ServicesEvent.SaveEdit(service)) }
                                        if (d.saving) disabled()
                                    }) { if (d.saving) Spinner() else Text("Save") }
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
