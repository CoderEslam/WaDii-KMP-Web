package com.wadii.screens.savedOffers

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.screens.providerDetail.ProviderDetailScreen
import com.wadii.ui.LoadingScreen
import com.wadii.viewmodel.UiState
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.dom.*

class SavedOffersScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = rememberScreenModel { SavedOffersScreenModel() }

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Saved Offers") }

            when (val s = model.state) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Error -> Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) { Text(s.message) }
                is UiState.Success -> {
                    val offers = s.data.offers
                    if (offers.isEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                            P(attrs = { classes("text-5xl", "mb-3") }) { Text("🏷️") }
                            P(attrs = { classes("text-slate-500") }) { Text("No saved offers yet. Browse offers to save them.") }
                        }
                    } else {
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                            offers.forEach { offer ->
                                SavedOfferCard(
                                    offer = offer,
                                    onRemove = { model.onEvent(SavedOffersEvent.Remove(offer)) },
                                    onProviderClick = { offer.offer .provider?.let { navigator.push(ProviderDetailScreen(it.id)) } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedOfferCard(offer: SavedOffer, onRemove: () -> Unit, onProviderClick: () -> Unit) {
    Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5") }) {
        Div(attrs = { classes("flex", "items-start", "justify-between") }) {
            Div(attrs = { classes("flex-1") }) {
                P(attrs = { classes("font-semibold", "text-slate-800", "mb-1") }) { Text(offer.offer.title) }
                P(attrs = { classes("text-sm", "text-slate-500", "mb-3") }) { Text(offer.offer.description) }
                offer.offer.provider .services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                    Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mb-2") }) {
                        svcs.forEach { s ->
                            Span(attrs = { classes("text-xs", "bg-slate-100", "text-slate-600", "px-2", "py-0.5", "rounded-full") }) { Text(s.name) }
                        }
                    }
                }
                P(attrs = { classes("text-xs", "text-slate-400") }) { Text("Expires: ${offer.offer.endDate.take(10)}") }
            }
            Button(attrs = {
                classes("ml-3", "p-2", "text-amber-500", "bg-amber-50", "rounded-lg", "hover:bg-amber-100")
                onClick { onRemove() }
            }) { Text("⭐") }
        }
        offer.offer.provider?.let {
            Button(attrs = {
                classes("mt-3", "text-xs", "text-amber-600", "font-medium", "hover:underline")
                onClick { onProviderClick() }
            }) { Text("By ${it.name} →") }
        }
    }
}
