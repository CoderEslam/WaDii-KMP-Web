package com.wadii.pages.admin.provider

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.ui.LoadingScreen
import org.jetbrains.compose.web.dom.*

class ProviderRequestsScreen : Screen {

    @Composable
    override fun Content() {
        val model = koinScreenModel<ProviderRequestsViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Provider Requests") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Div(attrs = {
                    classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500")
                }) { Text(state.error!!) }

                else -> {
                    val requests = state.requests
                    if (requests.isEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                            P(attrs = { classes("text-5xl", "mb-3") }) { Text("📋") }
                            P(attrs = { classes("text-slate-500") }) { Text("No pending requests.") }
                        }
                    } else {
                        Div(attrs = { classes("space-y-4") }) {
                            requests.forEach { req ->
                                Div(attrs = {
                                    classes("bg-white", "border", "border-slate-200", "rounded-2xl", "p-6", "shadow-sm")
                                }) {
                                    Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                                        Div(attrs = { classes("space-y-1") }) {
                                            H3(attrs = { classes("font-semibold", "text-slate-800", "text-lg") }) { Text(req.name) }
                                            P(attrs = { classes("text-sm", "text-slate-500") }) { Text("📞 ${req.phoneNumber}") }
                                            P(attrs = { classes("text-sm", "text-slate-500") }) { Text("📍 ${req.address}") }
//                                            req.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
//                                                Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
//                                                    svcs.forEach { s ->
//                                                        Span(attrs = {
//                                                            classes("px-2", "py-0.5", "bg-amber-50", "text-amber-700", "text-xs", "rounded-full")
//                                                        }) { Text(s.name) }
//                                                    }
//                                                }
//                                            }
//                                            req.links?.let {
//                                                if (it.isNotBlank()) P(attrs = { classes("text-sm", "text-amber-600", "mt-1") }) { Text("🔗 $it") }
//                                            }
                                        }
//                                        Button(attrs = {
//                                            classes("flex", "items-center", "gap-2", "px-4", "py-2", "bg-green-500", "text-white", "text-sm", "font-medium", "rounded-xl", "hover:bg-green-600", "disabled:opacity-60")
//                                            onClick { model.onEvent(ProviderRequestsEvent.Accept(req)) }
//                                            if (acceptingId == req.id) disabled()
//                                        }) {
//                                            if (acceptingId == req.id) Spinner() else Text("✓ Accept")
//                                        }
                                    }
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
        }
    }
}
