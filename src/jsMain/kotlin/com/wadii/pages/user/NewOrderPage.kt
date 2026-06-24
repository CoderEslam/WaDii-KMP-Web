package com.wadii.pages.user

import androidx.compose.runtime.*
import com.wadii.api.apiGetAllServices
import com.wadii.api.apiInsertOrder
import com.wadii.model.Service
import com.wadii.router.Route
import com.wadii.state.AppState
import com.wadii.ui.InputField
import com.wadii.ui.Spinner
import com.wadii.ui.TextArea
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun NewOrderPage() {
    var carModelYear by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var selectedServices by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var spareParts by remember { mutableStateOf(listOf("")) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { services = apiGetAllServices() }

    fun submit() {
        if (loading) return
        loading = true
        scope.launch {
            val body = mapOf(
                "carModelYear" to carModelYear,
                "comment" to comment,
                "date" to js("new Date().toISOString()"),
                "services" to selectedServices.map { mapOf("id" to it) },
                "spareParts" to spareParts.filter { it.isNotBlank() }.map { mapOf("sparePartName" to it) }
            )
            val order = apiInsertOrder(body)
            loading = false
            if (order != null) {
                AppState.toast("Order created!")
                AppState.navigate(Route.ORDERS)
            } else {
                AppState.toast("Failed to create order", true)
            }
        }
    }

    Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
        Div(attrs = { classes("flex", "items-center", "gap-3") }) {
            Button(attrs = {
                classes("text-slate-500", "hover:text-slate-700", "text-sm")
                onClick { AppState.navigate(Route.ORDERS) }
            }) { Text("← Back") }
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("New Order") }
        }

        Div(attrs = { classes("bg-white", "rounded-2xl", "p-6", "shadow-sm") }) {
            Div(attrs = { classes("space-y-5") }) {
                InputField("Vehicle (Make, Model, Year)", carModelYear, "Toyota Camry 2022", required = true) {
                    carModelYear = it
                }
                TextArea("Description", comment, "Describe what you need…", 4) { comment = it }

                // Services
                if (services.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-sm", "font-medium", "text-slate-700", "mb-2") }) {
                            Text("Services Needed")
                        }
                        Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                            services.forEach { s ->
                                val sel = s.id in selectedServices
                                Button(attrs = {
                                    attr("type", "button")
                                    classes("px-3", "py-1.5", "rounded-full", "text-sm", "transition-colors",
                                        if (sel) "bg-amber-500 text-white" else "bg-slate-100 text-slate-700 hover:bg-slate-200")
                                    onClick {
                                        selectedServices = if (sel) selectedServices - s.id else selectedServices + s.id
                                    }
                                }) { Text(s.name) }
                            }
                        }
                    }
                }

                // Spare parts
                Div {
                    P(attrs = { classes("text-sm", "font-medium", "text-slate-700", "mb-2") }) {
                        Text("Spare Parts Needed")
                    }
                    Div(attrs = { classes("space-y-2") }) {
                        spareParts.forEachIndexed { i, part ->
                            Div(attrs = { classes("flex", "gap-2") }) {
                                Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
                                    classes("flex-1", "px-4", "py-2", "border", "border-slate-300",
                                        "rounded-lg", "text-sm", "focus:outline-none", "focus:ring-2",
                                        "focus:ring-amber-400")
                                    attr("placeholder", "e.g. Brake pad")
                                    attr("value", part)
                                    onInput { spareParts = spareParts.toMutableList().also { l -> l[i] = it.value } }
                                })
                                if (spareParts.size > 1) {
                                    Button(attrs = {
                                        attr("type", "button")
                                        classes("px-3", "py-2", "text-red-400", "hover:text-red-600", "text-sm")
                                        onClick { spareParts = spareParts.toMutableList().also { l -> l.removeAt(i) } }
                                    }) { Text("✕") }
                                }
                            }
                        }
                        Button(attrs = {
                            attr("type", "button")
                            classes("text-sm", "text-amber-600", "hover:underline")
                            onClick { spareParts = spareParts + "" }
                        }) { Text("+ Add spare part") }
                    }
                }

                Button(attrs = {
                    classes("w-full", "py-3", "bg-amber-500", "hover:bg-amber-600",
                        "text-white", "font-semibold", "rounded-xl", "transition-colors",
                        "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2")
                    attr("type", "button")
                    onClick { submit() }
                    if (loading) disabled()
                }) { if (loading) Spinner() else Text("Submit Order") }
            }
        }
    }
}
