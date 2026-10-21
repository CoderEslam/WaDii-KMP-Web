package com.wadii.pages.shared.profile

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.BackButton
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.GhostButton
import com.wadii.ui.InputField
import com.wadii.ui.LoadingScreen
import com.wadii.ui.PrimaryButton
import com.wadii.ui.SecondaryButton
import com.wadii.ui.classNames
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.files.File

class RequestProviderScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<RequestProviderViewModel>()
        val s by model.state.collectAsState()

        LaunchedEffect(s.submittedSuccessfully) {
            if (s.submittedSuccessfully) navigator.pop()
        }

        fun pickFile(onFile: (File) -> Unit) {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = "image/jpeg,image/png,image/webp"
            input.onchange = { input.files?.asList()?.firstOrNull()?.let(onFile); null }
            input.click()
        }

        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                BackButton { navigator.pop() }
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Become a Seller") }
            }

            if (s.loading) {
                LoadingScreen()
            } else {
                s.error?.let { Alert(variant = AlertVariant.Danger, body = it) }

                Card(classes = "p-6") {
                    H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Shop Info") }
                    Div(attrs = { classes("space-y-4") }) {
                        InputField("Shop Name", s.name, required = true, error = s.errors["name"]) { model.onEvent(RequestProviderEvent.SetName(it)) }
                        InputField("Address", s.address, required = true, error = s.errors["address"]) { model.onEvent(RequestProviderEvent.SetAddress(it)) }
                        InputField("Phone Number", s.phoneNumber, "+20 100 123 4567", type = "tel", required = true, error = s.errors["phone"]) {
                            model.onEvent(RequestProviderEvent.SetPhoneNumber(it))
                        }
                    }
                }

                Card(classes = "p-6") {
                    H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Services") }
                    if (s.allServices.isEmpty()) {
                        P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("No services available yet.") }
                    } else {
                        Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                            s.allServices.forEach { svc ->
                                val selected = svc.id in s.selectedServiceIds
                                Span(attrs = {
                                    style { property("cursor", "pointer") }
                                    onClick { model.onEvent(RequestProviderEvent.ToggleService(svc.id)) }
                                }) {
                                    Badge(svc.name, variant = if (selected) BadgeVariant.Brand else BadgeVariant.Alternative, pill = true)
                                }
                            }
                        }
                        s.errors["services"]?.let { P(attrs = { classes("field-error", "mt-2"); attr("role", "alert") }) { Text(it) } }
                    }
                }

                Card(classes = "p-6") {
                    H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) { Text("Links") }
                    Div(attrs = { classes("space-y-3") }) {
                        s.links.forEachIndexed { linkIndex, link ->
                            Div(attrs = { classes("flex", "items-end", "gap-2") }) {
                                Div(attrs = { classes("flex-1") }) {
                                    InputField("URL", link, type = "url", placeholder = "https://") {
                                        model.onEvent(RequestProviderEvent.SetLink(linkIndex, it))
                                    }
                                }
                                GhostButton("🗑️") { model.onEvent(RequestProviderEvent.RemoveLink(linkIndex)) }
                            }
                        }
                        s.errors["links"]?.let { P(attrs = { classes("field-error"); attr("role", "alert") }) { Text(it) } }
                        SecondaryButton("+ Add link", fullWidth = true) { model.onEvent(RequestProviderEvent.AddLink) }
                    }
                }

                Card(classes = "p-6") {
                    H2(attrs = { classes("font-semibold", "text-heading", "mb-1") }) { Text("Verification Documents") }
                    P(attrs = { classes("text-xs", "text-body-subtle", "mb-4") }) {
                        Text("JPG, PNG or WebP, up to 5 MB each. Only our review team can see these.")
                    }
                    Div(attrs = { classes("grid", "grid-cols-1", "sm:grid-cols-2", "gap-4") }) {
                        FilePickerField("Front ID Image", s.frontIdImage?.name, s.errors["frontId"]) {
                            pickFile { model.onEvent(RequestProviderEvent.SetFrontIdImage(it)) }
                        }
                        FilePickerField("Back ID Image", s.backIdImage?.name, s.errors["backId"]) {
                            pickFile { model.onEvent(RequestProviderEvent.SetBackIdImage(it)) }
                        }
                        FilePickerField("Tax Card Front", s.taxCardFront?.name, s.errors["taxFront"]) {
                            pickFile { model.onEvent(RequestProviderEvent.SetTaxCardFront(it)) }
                        }
                        FilePickerField("Tax Card Back", s.taxCardBack?.name, s.errors["taxBack"]) {
                            pickFile { model.onEvent(RequestProviderEvent.SetTaxCardBack(it)) }
                        }
                    }
                }

                Div(attrs = { classes("flex", "gap-3") }) {
                    SecondaryButton("Cancel", fullWidth = true) { navigator.pop() }
                    PrimaryButton("Submit Request", loading = s.submitting, fullWidth = true) {
                        model.onEvent(RequestProviderEvent.Submit)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilePickerField(label: String, fileName: String?, error: String?, onClick: () -> Unit) {
    Div(attrs = { classes("space-y-2") }) {
        Label(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text("$label *") }
        Button(attrs = {
            classes(*classNames(
                "w-full", "px-4", "py-2.5", "min-h-11", "border", "border-default-medium", "rounded-neu-base",
                "bg-surface", "shadow-neu-sm", "hover:shadow-neu-md", "active:shadow-neu-inset",
                "transition-all", "text-sm", "text-body", "text-left", "truncate",
                if (error != null) "input-invalid" else null
            ))
            style { property("cursor", "pointer") }
            onClick { onClick() }
        }) { Text(fileName?.let { "✅ $it" } ?: "📎 Choose file") }
        error?.let { P(attrs = { classes("field-error"); attr("role", "alert") }) { Text(it) } }
    }
}
