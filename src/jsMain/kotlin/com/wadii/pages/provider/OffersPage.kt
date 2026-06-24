package com.wadii.pages.provider

import androidx.compose.runtime.*
import com.wadii.model.Offer
import com.wadii.model.Service
import com.wadii.ui.InputField
import com.wadii.ui.LoadingScreen
import com.wadii.ui.Spinner
import com.wadii.ui.TextArea
import com.wadii.viewmodel.ProviderOffersEvent
import com.wadii.viewmodel.ProviderOffersScreenModel
import com.wadii.viewmodel.UiState
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun ProviderOffersPage() {
    val model = rememberScreenModel { ProviderOffersScreenModel() }

    Div(attrs = { classes("space-y-6") }) {
        Div(attrs = { classes("flex", "items-center", "justify-between") }) {
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("My Offers") }
            Button(attrs = {
                classes("flex", "items-center", "gap-2", "px-4", "py-2", "bg-amber-500", "text-white", "rounded-xl", "text-sm", "font-medium", "hover:bg-amber-600")
                onClick { model.onEvent(ProviderOffersEvent.ShowModal(null)) }
            }) { Text("+ New Offer") }
        }

        when (val s = model.state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-500") }) { Text(s.message) }
            is UiState.Success -> {
                val d = s.data
                if (d.offers.isEmpty()) {
                    Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                        P(attrs = { classes("text-5xl", "mb-3") }) { Text("🏷️") }
                        P(attrs = { classes("text-slate-500") }) { Text("No offers yet. Create your first offer!") }
                    }
                } else {
                    Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                        d.offers.forEach { offer ->
                            Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5") }) {
                                Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                                    Div(attrs = { classes("flex-1") }) {
                                        P(attrs = { classes("font-semibold", "text-slate-800") }) { Text(offer.title) }
                                        P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(offer.description) }
                                        P(attrs = { classes("text-xs", "text-slate-400", "mt-2") }) { Text("Expires: ${offer.endDate.take(10)}") }
                                    }
                                    Div(attrs = { classes("flex", "gap-1", "ml-3") }) {
                                        Button(attrs = {
                                            classes("p-2", "text-slate-400", "hover:text-amber-500", "hover:bg-amber-50", "rounded-lg")
                                            onClick { model.onEvent(ProviderOffersEvent.ShowModal(offer)) }
                                        }) { Text("✏️") }
                                        Button(attrs = {
                                            classes("p-2", "text-slate-400", "hover:text-red-500", "hover:bg-red-50", "rounded-lg")
                                            onClick { model.onEvent(ProviderOffersEvent.Delete(offer.id)) }
                                        }) { Text("🗑️") }
                                    }
                                }
                            }
                        }
                    }
                }
                if (d.showModal) {
                    OfferModal(
                        offer = d.editOffer,
                        services = d.services,
                        saving = d.saving,
                        onClose = { model.onEvent(ProviderOffersEvent.CloseModal) },
                        onSave = { title, desc, endDate, selectedServices ->
                            model.onEvent(ProviderOffersEvent.Save(d.editOffer, title, desc, endDate, selectedServices))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OfferModal(
    offer: Offer?,
    services: List<Service>,
    saving: Boolean,
    onClose: () -> Unit,
    onSave: (String, String, String, Set<Long>) -> Unit
) {
    var title by remember(offer?.id) { mutableStateOf(offer?.title ?: "") }
    var description by remember(offer?.id) { mutableStateOf(offer?.description ?: "") }
    var endDate by remember(offer?.id) { mutableStateOf(offer?.endDate?.take(10) ?: "") }
    var selectedServices by remember(offer?.id) { mutableStateOf(offer?.services?.map { it.id }?.toSet() ?: emptySet<Long>()) }

    Div(attrs = { classes("fixed", "inset-0", "z-50", "bg-black/50", "flex", "items-center", "justify-center", "p-4") }) {
        Div(attrs = { classes("bg-white", "rounded-2xl", "w-full", "max-w-md", "p-6") }) {
            H2(attrs = { classes("text-lg", "font-semibold", "text-slate-800", "mb-4") }) {
                Text(if (offer != null) "Edit Offer" else "New Offer")
            }
            Div(attrs = { classes("space-y-4") }) {
                InputField("Title", title, required = true) { title = it }
                TextArea("Description", description, rows = 3) { description = it }
                InputField("End Date", endDate, type = "date", required = true) { endDate = it }
                if (services.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-sm", "font-medium", "text-slate-700", "mb-2") }) { Text("Services") }
                        Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                            services.forEach { s ->
                                val sel = s.id in selectedServices
                                Button(attrs = {
                                    attr("type", "button")
                                    classes("px-3", "py-1", "rounded-full", "text-sm", "transition-colors")
                                    if (sel) classes("bg-amber-500", "text-white") else classes("bg-slate-100", "text-slate-700", "hover:bg-slate-200")
                                    onClick { selectedServices = if (sel) selectedServices - s.id else selectedServices + s.id }
                                }) { Text(s.name) }
                            }
                        }
                    }
                }
                Div(attrs = { classes("flex", "gap-3", "pt-2") }) {
                    Button(attrs = {
                        attr("type", "button")
                        classes("flex-1", "py-2.5", "border", "border-slate-300", "text-slate-700", "rounded-xl", "text-sm", "hover:bg-slate-50")
                        onClick { onClose() }
                    }) { Text("Cancel") }
                    Button(attrs = {
                        classes("flex-1", "py-2.5", "bg-amber-500", "text-white", "font-semibold", "rounded-xl", "text-sm", "hover:bg-amber-600", "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2")
                        attr("type", "button")
                        onClick { onSave(title, description, endDate, selectedServices) }
                        if (saving) disabled()
                    }) { if (saving) Spinner() else Text("Save") }
                }
            }
        }
    }
}
