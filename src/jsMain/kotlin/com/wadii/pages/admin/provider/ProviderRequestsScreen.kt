package com.wadii.pages.admin.provider

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.LoadingScreen
import org.jetbrains.compose.web.dom.*

class ProviderRequestsScreen : Screen {

    @Composable
    override fun Content() {
        val model = koinScreenModel<ProviderRequestsViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Seller Requests") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)

                else -> {
                    val requests = state.requests
                    if (requests.isEmpty()) {
                        EmptyState("📋", "No pending requests.")
                    } else {
                        Div(attrs = { classes("space-y-4") }) {
                            requests.forEach { req ->
                                Card(classes = "p-6") {
                                    Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                                        Div(attrs = { classes("space-y-1") }) {
                                            H3(attrs = { classes("font-semibold", "text-heading", "text-lg") }) { Text(req.name) }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("📞 ${req.phoneNumber}") }
                                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("📍 ${req.address}") }
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
                                                    P(attrs = { classes("text-xs", "text-body-subtle", "mb-1") }) { Text("Front ID") }
                                                    Img(src = "http://192.168.1.30:8080/uploads/$img", attrs = {
                                                        classes("h-20", "rounded-neu-base", "object-cover", "border", "border-default")
                                                    })
                                                }
                                            }
                                            req.backIdImage?.let { img ->
                                                Div {
                                                    P(attrs = { classes("text-xs", "text-body-subtle", "mb-1") }) { Text("Back ID") }
                                                    Img(src = "http://192.168.1.30:8080/uploads/$img", attrs = {
                                                        classes("h-20", "rounded-neu-base", "object-cover", "border", "border-default")
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
