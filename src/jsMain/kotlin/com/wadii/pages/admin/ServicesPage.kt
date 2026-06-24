package com.wadii.pages.admin

import androidx.compose.runtime.*
import com.wadii.api.apiDeleteService
import com.wadii.api.apiGetAllServices
import com.wadii.api.apiInsertService
import com.wadii.api.apiUpdateService
import com.wadii.model.Service
import com.wadii.state.AppState
import com.wadii.ui.InputField
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.Spinner
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun ServicesPage() {
    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var editingId by remember { mutableStateOf<Long?>(null) }
    var editName by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var adding by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val reload: () -> Unit = { scope.launch { services = apiGetAllServices(); loading = false } }
    LaunchedEffect(Unit) { reload() }

    Div(attrs = { classes("space-y-6") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Services") }

        // Add new service
        Div(attrs = { classes("bg-white", "rounded-2xl", "p-5", "shadow-sm") }) {
            H2(attrs = { classes("font-semibold", "text-slate-800", "mb-3") }) { Text("Add Service") }
            Div(attrs = { classes("flex", "gap-3") }) {
                Div(attrs = { classes("flex-1") }) {
                    InputField("Service name", newName, required = true) { newName = it }
                }
                Button(attrs = {
                    classes("px-5", "py-2.5", "bg-amber-500", "text-white", "font-medium",
                        "rounded-xl", "text-sm", "hover:bg-amber-600", "disabled:opacity-60",
                        "flex", "items-center", "gap-2", "self-end")
                    attr("type", "button")
                    onClick {
                        if (adding || newName.isBlank()) return@onClick
                        adding = true
                        scope.launch {
                            val result = apiInsertService(newName)
                            adding = false
                            if (result != null) { newName = ""; reload(); AppState.toast("Service added!") }
                            else AppState.toast("Failed to add", true)
                        }
                    }
                    if (adding) disabled()
                }) { if (adding) Spinner() else Text("Add") }
            }
        }

        // Services list
        if (loading) { LoadingSkeletons() }
        else if (services.isEmpty()) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                P(attrs = { classes("text-5xl", "mb-3") }) { Text("🔧") }
                P(attrs = { classes("text-slate-500") }) { Text("No services yet.") }
            }
        } else {
            Div(attrs = { classes("bg-white", "rounded-2xl", "shadow-sm", "divide-y", "divide-slate-100") }) {
                services.forEach { service ->
                    Div(attrs = { classes("flex", "items-center", "gap-3", "px-5", "py-4") }) {
                        if (editingId == service.id) {
                            Div(attrs = { classes("flex-1") }) {
                                InputField("Name", editName) { editName = it }
                            }
                            Button(attrs = {
                                classes("px-3", "py-1.5", "bg-amber-500", "text-white", "text-sm",
                                    "rounded-lg", "hover:bg-amber-600", "disabled:opacity-60",
                                    "flex", "items-center", "gap-1")
                                onClick {
                                    if (saving) return@onClick
                                    saving = true
                                    scope.launch {
                                        val result = apiUpdateService(service.id, editName)
                                        saving = false
                                        if (result != null) { editingId = null; reload(); AppState.toast("Updated!") }
                                        else AppState.toast("Failed to update", true)
                                    }
                                }
                                if (saving) disabled()
                            }) { if (saving) Spinner() else Text("Save") }
                            Button(attrs = {
                                classes("px-3", "py-1.5", "border", "border-slate-300", "text-slate-600",
                                    "text-sm", "rounded-lg", "hover:bg-slate-50")
                                onClick { editingId = null }
                            }) { Text("Cancel") }
                        } else {
                            Div(attrs = { classes("flex", "items-center", "gap-3", "flex-1") }) {
                                Div(attrs = { classes("w-8", "h-8", "bg-amber-50", "rounded-lg",
                                    "flex", "items-center", "justify-center") }) {
                                    Span(attrs = { classes("text-sm") }) { Text("🔧") }
                                }
                                P(attrs = { classes("font-medium", "text-slate-800") }) { Text(service.name) }
                            }
                            Button(attrs = {
                                classes("p-1.5", "text-slate-400", "hover:text-amber-500",
                                    "hover:bg-amber-50", "rounded-lg", "transition-colors")
                                onClick { editingId = service.id; editName = service.name }
                            }) { Text("✏️") }
                            Button(attrs = {
                                classes("p-1.5", "text-slate-400", "hover:text-red-500",
                                    "hover:bg-red-50", "rounded-lg", "transition-colors")
                                onClick {
                                    scope.launch {
                                        if (apiDeleteService(service.id)) { reload(); AppState.toast("Deleted") }
                                        else AppState.toast("Failed to delete", true)
                                    }
                                }
                            }) { Text("🗑️") }
                        }
                    }
                }
            }
        }
    }
}
