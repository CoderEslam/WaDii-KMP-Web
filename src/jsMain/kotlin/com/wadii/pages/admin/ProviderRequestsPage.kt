package com.wadii.pages.admin

import androidx.compose.runtime.*
import com.wadii.api.apiAcceptRequest
import com.wadii.api.apiGetAllRequests
import com.wadii.model.ProviderRequest
import com.wadii.state.AppState
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.Spinner
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun ProviderRequestsPage() {
    var requests by remember { mutableStateOf<List<ProviderRequest>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var accepting by remember { mutableStateOf<Long?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        requests = apiGetAllRequests()
        loading = false
    }

    Div(attrs = { classes("space-y-6") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Provider Requests") }

        if (loading) { LoadingSkeletons() }
        else if (requests.isEmpty()) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                P(attrs = { classes("text-5xl", "mb-3") }) { Text("📋") }
                P(attrs = { classes("text-slate-500") }) { Text("No pending requests.") }
            }
        } else {
            Div(attrs = { classes("space-y-4") }) {
                requests.forEach { req ->
                    Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-2xl",
                        "p-6", "shadow-sm") }) {
                        Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                            Div(attrs = { classes("space-y-1") }) {
                                H3(attrs = { classes("font-semibold", "text-slate-800", "text-lg") }) { Text(req.name) }
                                P(attrs = { classes("text-sm", "text-slate-500") }) {
                                    Text("📞 ${req.phoneNumber}")
                                }
                                P(attrs = { classes("text-sm", "text-slate-500") }) {
                                    Text("📍 ${req.address}")
                                }
                                req.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                    Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                        svcs.forEach { s ->
                                            Span(attrs = { classes("px-2", "py-0.5", "bg-amber-50",
                                                "text-amber-700", "text-xs", "rounded-full") }) { Text(s.name) }
                                        }
                                    }
                                }
                                req.links?.let { l ->
                                    if (l.isNotBlank()) {
                                        P(attrs = { classes("text-sm", "text-amber-600", "mt-1") }) { Text("🔗 $l") }
                                    }
                                }
                            }
                            Button(attrs = {
                                classes("flex", "items-center", "gap-1.5", "px-4", "py-2",
                                    "bg-green-500", "text-white", "text-sm", "font-medium",
                                    "rounded-xl", "hover:bg-green-600", "disabled:opacity-60",
                                    "flex", "items-center", "gap-2")
                                onClick {
                                    accepting = req.id
                                    scope.launch {
                                        if (apiAcceptRequest(req.id)) {
                                            requests = requests.filter { it.id != req.id }
                                            AppState.toast("Provider request accepted!")
                                        } else {
                                            AppState.toast("Failed to accept", true)
                                        }
                                        accepting = null
                                    }
                                }
                                if (accepting == req.id) disabled()
                            }) {
                                if (accepting == req.id) Spinner()
                                else Text("✓ Accept")
                            }
                        }

                        // ID Images
                        if (req.frontIdImage != null || req.backIdImage != null) {
                            Div(attrs = { classes("mt-4", "flex", "gap-3") }) {
                                req.frontIdImage?.let { img ->
                                    Div {
                                        P(attrs = { classes("text-xs", "text-slate-500", "mb-1") }) { Text("Front ID") }
                                        Img(src = "http://192.168.1.30:8080/uploads/$img", attrs = {
                                            classes("h-20", "rounded-lg", "object-cover", "border", "border-slate-200")
                                        })
                                    }
                                }
                                req.backIdImage?.let { img ->
                                    Div {
                                        P(attrs = { classes("text-xs", "text-slate-500", "mb-1") }) { Text("Back ID") }
                                        Img(src = "http://192.168.1.30:8080/uploads/$img", attrs = {
                                            classes("h-20", "rounded-lg", "object-cover", "border", "border-slate-200")
                                        })
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
