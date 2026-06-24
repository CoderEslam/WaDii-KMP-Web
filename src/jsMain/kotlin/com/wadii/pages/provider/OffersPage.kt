package com.wadii.pages.provider

import androidx.compose.runtime.*
import com.wadii.api.*
import com.wadii.model.Offer
import com.wadii.model.Service
import com.wadii.state.AppState
import com.wadii.ui.InputField
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.Spinner
import com.wadii.ui.TextArea
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun ProviderOffersPage() {
    var offers by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showModal by remember { mutableStateOf(false) }
    var editOffer by remember { mutableStateOf<Offer?>(null) }
    val scope = rememberCoroutineScope()

    val reload: () -> Unit = {
        scope.launch {
            offers = apiGetAllOffers()
            services = apiGetAllServices()
            loading = false
        }
    }

    LaunchedEffect(Unit) { reload() }

    Div(attrs = { classes("space-y-6") }) {
        Div(attrs = { classes("flex", "items-center", "justify-between") }) {
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("My Offers") }
            Button(attrs = {
                classes("flex", "items-center", "gap-2", "px-4", "py-2", "bg-amber-500",
                    "text-white", "rounded-xl", "text-sm", "font-medium", "hover:bg-amber-600")
                onClick { editOffer = null; showModal = true }
            }) { Text("+ New Offer") }
        }

        if (loading) { LoadingSkeletons() }
        else if (offers.isEmpty()) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                P(attrs = { classes("text-5xl", "mb-3") }) { Text("🏷️") }
                P(attrs = { classes("text-slate-500") }) { Text("No offers yet. Create your first offer!") }
            }
        } else {
            Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                offers.forEach { offer ->
                    Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5") }) {
                        Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                            Div(attrs = { classes("flex-1") }) {
                                P(attrs = { classes("font-semibold", "text-slate-800") }) { Text(offer.title) }
                                P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(offer.description) }
                                P(attrs = { classes("text-xs", "text-slate-400", "mt-2") }) {
                                    Text("Expires: ${offer.endDate.take(10)}")
                                }
                            }
                            Div(attrs = { classes("flex", "gap-1", "ml-3") }) {
                                Button(attrs = {
                                    classes("p-2", "text-slate-400", "hover:text-amber-500", "hover:bg-amber-50", "rounded-lg")
                                    onClick { editOffer = offer; showModal = true }
                                }) { Text("✏️") }
                                Button(attrs = {
                                    classes("p-2", "text-slate-400", "hover:text-red-500", "hover:bg-red-50", "rounded-lg")
                                    onClick {
                                        scope.launch {
                                            if (apiDeleteOffer(offer.id)) { reload(); AppState.toast("Deleted") }
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

    if (showModal) {
        OfferModal(editOffer, services,
            onClose = { showModal = false },
            onSaved = { reload(); showModal = false }
        )
    }
}

@Composable
private fun OfferModal(
    offer: Offer?,
    services: List<Service>,
    onClose: () -> Unit,
    onSaved: () -> Unit
) {
    var title by remember { mutableStateOf(offer?.title ?: "") }
    var description by remember { mutableStateOf(offer?.description ?: "") }
    var endDate by remember { mutableStateOf(offer?.endDate?.take(10) ?: "") }
    var selectedServices by remember { mutableStateOf(offer?.services?.map { it.id }?.toSet() ?: emptySet<Long>()) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun save() {
        if (loading) return
        loading = true
        scope.launch {
            val body = buildMap<String, Any?> {
                put("title", title); put("description", description); put("endDate", endDate)
                put("services", selectedServices.map { mapOf("id" to it) })
                offer?.let { put("id", it.id) }
            }
            val result = if (offer != null) apiUpdateOffer(body) else apiInsertOffer(body)
            loading = false
            if (result != null) {
                AppState.toast(if (offer != null) "Offer updated!" else "Offer created!")
                onSaved()
            } else {
                AppState.toast("Failed to save offer", true)
            }
        }
    }

    Div(attrs = { classes("fixed", "inset-0", "z-50", "bg-black/50", "flex", "items-center",
        "justify-center", "p-4") }) {
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
                                    classes("px-3", "py-1", "rounded-full", "text-sm", "transition-colors",
                                        if (sel) "bg-amber-500 text-white" else "bg-slate-100 text-slate-700 hover:bg-slate-200")
                                    onClick {
                                        selectedServices = if (sel) selectedServices - s.id else selectedServices + s.id
                                    }
                                }) { Text(s.name) }
                            }
                        }
                    }
                }

                Div(attrs = { classes("flex", "gap-3", "pt-2") }) {
                    Button(attrs = {
                        attr("type", "button")
                        classes("flex-1", "py-2.5", "border", "border-slate-300", "text-slate-700",
                            "rounded-xl", "text-sm", "hover:bg-slate-50")
                        onClick { onClose() }
                    }) { Text("Cancel") }
                    Button(attrs = {
                        classes("flex-1", "py-2.5", "bg-amber-500", "text-white", "font-semibold",
                            "rounded-xl", "text-sm", "hover:bg-amber-600", "disabled:opacity-60",
                            "flex", "items-center", "justify-center", "gap-2")
                        attr("type", "button")
                        onClick { save() }
                        if (loading) disabled()
                    }) { if (loading) Spinner() else Text("Save") }
                }
            }
        }
    }
}
