package com.wadii.pages.admin.provider

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.domain.model.providerRequests.ProviderRequestModel
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.DangerButton
import com.wadii.ui.EmptyState
import com.wadii.ui.ImageLightbox
import com.wadii.ui.InputField
import com.wadii.ui.LoadingScreen
import com.wadii.ui.Modal
import com.wadii.ui.ModalVariant
import com.wadii.ui.SecondaryButton
import com.wadii.ui.SuccessButton
import com.wadii.utils.Constants
import com.wadii.utils.fetchProtectedImage
import org.jetbrains.compose.web.dom.*

class ProviderRequestsScreen : Screen {

    @Composable
    override fun Content() {
        val model = koinScreenModel<ProviderRequestsViewModel>()
        val state by model.state.collectAsState()
        var lightboxImage by remember { mutableStateOf<String?>(null) }

        Div(attrs = { classes("space-y-6") }) {
            Div {
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Seller Requests") }
                P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) {
                    Text("Accepting creates the shop and makes the applicant a seller. Rejecting tells them why.")
                }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)

                state.requests.isEmpty() -> EmptyState("📋", "No pending requests.")

                else -> Div(attrs = { classes("space-y-4", "stagger") }) {
                    state.requests.forEach { req ->
                        val working = state.acceptingId == req.id || state.rejectingId == req.id
                        Card(classes = "p-6") {
                            Div(attrs = { classes("flex", "flex-col", "sm:flex-row", "sm:items-start", "justify-between", "gap-4") }) {
                                Div(attrs = { classes("space-y-1", "min-w-0") }) {
                                    H3(attrs = { classes("font-semibold", "text-heading", "text-lg") }) { Text(req.name) }
                                    val applicant = "${req.user.firstName} ${req.user.lastName}".trim()
                                    if (applicant.isNotBlank()) {
                                        P(attrs = { classes("text-sm", "text-body") }) { Text("👤 $applicant · ${req.user.email}") }
                                    }
                                    if (req.phoneNumber.isNotBlank()) P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("📞 ${req.phoneNumber}") }
                                    if (req.address.isNotBlank()) P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("📍 ${req.address}") }
                                    req.services.takeIf { it.isNotEmpty() }?.let { svcs ->
                                        Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                                            svcs.forEach { s -> Badge(s.name, variant = BadgeVariant.Warning, pill = true) }
                                        }
                                    }
                                    req.links.takeIf { it.isNotEmpty() }?.let { links ->
                                        Div(attrs = { classes("space-y-1", "mt-1") }) {
                                            links.forEach { link ->
                                                A(href = link, attrs = {
                                                    classes("text-sm", "text-fg-brand", "hover:underline", "block", "break-all")
                                                    attr("target", "_blank")
                                                    attr("rel", "noopener noreferrer")
                                                }) { Text("🔗 $link") }
                                            }
                                        }
                                    }
                                }
                                Div(attrs = { classes("flex", "items-center", "gap-2", "flex-shrink-0") }) {
                                    SuccessButton("Accept", loading = state.acceptingId == req.id, disabled = working, icon = "✓") {
                                        model.onEvent(ProviderRequestsEvent.Accept(req))
                                    }
                                    DangerButton("Reject", disabled = working) { model.onEvent(ProviderRequestsEvent.Reject(req)) }
                                }
                            }

                            // Private documents: fetched with the admin's token, never linked directly.
                            val docs = listOf(
                                Triple("front-id", "ID (front)", req.frontIdImage),
                                Triple("back-id", "ID (back)", req.backIdImage),
                                Triple("tax-front", "Tax card (front)", req.taxCardFront),
                                Triple("tax-back", "Tax card (back)", req.taxCardBack)
                            ).filter { it.third != null }
                            if (docs.isNotEmpty()) {
                                Div(attrs = { classes("mt-4", "flex", "flex-wrap", "gap-3") }) {
                                    docs.forEach { (type, label, _) -> ProtectedDocument(req, type, label) { lightboxImage = it } }
                                }
                            }
                        }
                    }
                }
            }
        }

        Modal(
            open = state.rejectDraft != null,
            title = "Reject ${state.rejectDraft?.name ?: "application"}?",
            onDismiss = { model.onEvent(ProviderRequestsEvent.DismissReject) },
            variant = ModalVariant.Form,
            footer = {
                SecondaryButton("Cancel", disabled = state.rejectingId != 0L) { model.onEvent(ProviderRequestsEvent.DismissReject) }
                DangerButton("Reject", loading = state.rejectingId != 0L, onClick = { model.onEvent(ProviderRequestsEvent.ConfirmReject) })
            }
        ) {
            InputField(
                "Reason (optional)", state.rejectReason, "e.g. The tax card photo is unreadable",
                hint = "The applicant sees this in their notification."
            ) { model.onEvent(ProviderRequestsEvent.SetReason(it)) }
        }

        ImageLightbox(lightboxImage) { lightboxImage = null }
    }
}

/** One document thumbnail, loaded through the authenticated endpoint; click to enlarge. */
@Composable
private fun ProtectedDocument(req: ProviderRequestModel, type: String, label: String, onOpen: (String) -> Unit) {
    var src by remember(req.id, type) { mutableStateOf<String?>(null) }
    var failed by remember(req.id, type) { mutableStateOf(false) }
    LaunchedEffect(req.id, type) {
        val url = fetchProtectedImage(Constants.PROVIDER_DOCUMENT(req.id, type))
        if (url == null) failed = true else src = url
    }
    Div {
        P(attrs = { classes("text-xs", "text-body-subtle", "mb-1") }) { Text(label) }
        when {
            src != null -> Img(src = src!!, alt = label, attrs = {
                classes("h-20", "rounded-neu-base", "object-cover", "border", "border-default", "hover:shadow-neu-sm", "transition-all")
                style { property("cursor", "pointer") }
                onClick { onOpen(src!!) }
            })
            failed -> P(attrs = { classes("text-xs", "text-fg-danger") }) { Text("Couldn't load") }
            else -> Div(attrs = { classes("h-20", "w-28", "skeleton", "rounded-neu-base") }) {}
        }
    }
}
