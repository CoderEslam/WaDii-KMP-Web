package com.wadii.pages.provider.offers

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.service.Service
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.InputField
import com.wadii.ui.LoadingScreen
import com.wadii.ui.Modal
import com.wadii.ui.ModalVariant
import com.wadii.ui.PageHeader
import com.wadii.ui.PrimaryButton
import com.wadii.ui.SecondaryButton
import com.wadii.ui.TextArea
import org.jetbrains.compose.web.dom.*

class ProviderOffersScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<ProviderOffersViewModel>()
        Div(attrs = { classes("space-y-6") }) {
            PageHeader("My Deals", "+ New Deal") { model.onEvent(ProviderOffersEvent.ShowModal(null)) }

            val state by model.state.collectAsState()
            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)

                else -> {
                    if (state.offers.isEmpty()) {
                        EmptyState("🏷️", "No deals yet. Create your first deal!")
                    } else {
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                            state.offers.forEach { offer ->
                                Card(classes = "p-5") {
                                    Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                                        Div(attrs = { classes("flex-1") }) {
                                            P(attrs = { classes("font-semibold", "text-heading") }) { Text(offer.title) }
                                            P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) { Text(offer.description) }
                                            P(attrs = { classes("text-xs", "text-body-subtle", "mt-2") }) { Text("Expires: ${offer.endDate.take(10)}") }
                                        }
                                        Div(attrs = { classes("flex", "gap-1", "ml-3") }) {
                                            GhostButton("✏️") { model.onEvent(ProviderOffersEvent.ShowModal(offer)) }
                                            GhostButton("🗑️") { model.onEvent(ProviderOffersEvent.Delete(offer.id.toLong())) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Modal(
                        open = state.showModal,
                        title = if (state.editOffer != null) "Edit Deal" else "New Deal",
                        variant = ModalVariant.Form,
                        onDismiss = { model.onEvent(ProviderOffersEvent.CloseModal) }
                    ) {
                        OfferModalContent(
                            offer = state.editOffer,
                            services = state.services,
                            saving = state.saving,
                            onClose = { model.onEvent(ProviderOffersEvent.CloseModal) },
                            onSave = { title, desc, endDate, selectedServices ->
                                model.onEvent(
                                    ProviderOffersEvent.Save(
                                        state.editOffer,
                                        title,
                                        desc,
                                        endDate,
                                        selectedServices
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OfferModalContent(
    offer: OfferResponse?,
    services: List<Service>,
    saving: Boolean,
    onClose: () -> Unit,
    onSave: (String, String, String, Set<Long>) -> Unit
) {
    var title by remember(offer?.id) { mutableStateOf(offer?.title ?: "") }
    var description by remember(offer?.id) { mutableStateOf(offer?.description ?: "") }
    var endDate by remember(offer?.id) { mutableStateOf(offer?.endDate?.take(10) ?: "") }
    var selectedServices by remember(offer?.id) {
        mutableStateOf(services.map { it.id }.toSet())
    }

    InputField("Title", title, required = true) { title = it }
    TextArea("Description", description, rows = 3) { description = it }
    InputField("End Date", endDate, type = "date", required = true) { endDate = it }
    if (services.isNotEmpty()) {
        Div {
            P(attrs = { classes("text-sm", "font-medium", "text-heading", "mb-2") }) { Text("Services") }
            Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                services.forEach { s ->
                    val sel = s.id in selectedServices
                    Span(attrs = {
                        style { property("cursor", "pointer") }
                        onClick { selectedServices = if (sel) selectedServices - s.id else selectedServices + s.id }
                    }) {
                        Badge(s.name, variant = if (sel) BadgeVariant.Brand else BadgeVariant.Alternative, pill = true)
                    }
                }
            }
        }
    }
    Div(attrs = { classes("flex", "gap-3", "pt-2") }) {
        SecondaryButton("Cancel", fullWidth = true) { onClose() }
        PrimaryButton("Save", loading = saving, fullWidth = true) { onSave(title, description, endDate, selectedServices) }
    }
}
