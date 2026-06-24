package com.wadii.pages.user

import androidx.compose.runtime.*
import com.wadii.api.apiGetSavedOffers
import com.wadii.api.apiRemoveSavedOffer
import com.wadii.model.Offer
import com.wadii.state.AppState
import com.wadii.ui.LoadingSkeletons
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.*

@Composable
fun SavedOffersPage() {
    var offers by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        offers = apiGetSavedOffers()
        loading = false
    }

    Div(attrs = { classes("space-y-6") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Saved Offers") }

        if (loading) {
            LoadingSkeletons()
        } else if (offers.isEmpty()) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                P(attrs = { classes("text-5xl", "mb-3") }) { Text("🏷️") }
                P(attrs = { classes("text-slate-500") }) { Text("No saved offers yet. Browse offers to save them.") }
            }
        } else {
            Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                offers.forEach { offer ->
                    Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5") }) {
                        Div(attrs = { classes("flex", "items-start", "justify-between") }) {
                            Div(attrs = { classes("flex-1") }) {
                                P(attrs = { classes("font-semibold", "text-slate-800", "mb-1") }) { Text(offer.title) }
                                P(attrs = { classes("text-sm", "text-slate-500", "mb-3") }) { Text(offer.description) }
                                offer.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                                    Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mb-2") }) {
                                        svcs.forEach { s ->
                                            Span(attrs = { classes("text-xs", "bg-slate-100", "text-slate-600",
                                                "px-2", "py-0.5", "rounded-full") }) { Text(s.name) }
                                        }
                                    }
                                }
                                P(attrs = { classes("text-xs", "text-slate-400") }) {
                                    Text("Expires: ${offer.endDate.take(10)}")
                                }
                            }
                            Button(attrs = {
                                classes("ml-3", "p-2", "text-amber-500", "bg-amber-50",
                                    "rounded-lg", "hover:bg-amber-100")
                                onClick {
                                    scope.launch {
                                        if (apiRemoveSavedOffer(offer.id)) {
                                            offers = offers.filter { it.id != offer.id }
                                            AppState.toast("Removed from saved")
                                        } else {
                                            AppState.toast("Failed to remove", true)
                                        }
                                    }
                                }
                            }) { Text("⭐") }
                        }
                        offer.provider?.let { p ->
                            P(attrs = { classes("mt-3", "text-xs", "text-amber-600", "font-medium") }) {
                                Text("By ${p.name}")
                            }
                        }
                    }
                }
            }
        }
    }
}
