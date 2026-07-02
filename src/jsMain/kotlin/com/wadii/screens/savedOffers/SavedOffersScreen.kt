package com.wadii.screens.savedOffers

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.screens.providerDetail.ProviderDetailScreen
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.LoadingScreen
import org.jetbrains.compose.web.dom.*

class SavedOffersScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<SavedOffersViewModel>()
        val state by model.state.collectAsState()

        Div(attrs = { classes("space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Saved Offers") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                state.offers.isEmpty() -> EmptyState("🏷️", "No saved offers yet. Browse offers to save them.")
                else -> {
                    Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "gap-4") }) {
                        state.offers.forEach { offer ->
                            SavedOfferCard(
                                offer = offer,
                                onRemove = { model.onEvent(SavedOffersEvent.Remove(offer)) },
                                onProviderClick = {
                                    offer.offer.provider?.let {
                                        navigator.push(
                                            ProviderDetailScreen(it.id)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedOfferCard(offer: SavedOffer, onRemove: () -> Unit, onProviderClick: () -> Unit) {
    Card(classes = "p-5") {
        Div(attrs = { classes("flex", "items-start", "justify-between") }) {
            Div(attrs = { classes("flex-1") }) {
                P(attrs = { classes("font-semibold", "text-heading", "mb-1") }) { Text(offer.offer.title) }
                P(attrs = { classes("text-sm", "text-body-subtle", "mb-3") }) { Text(offer.offer.description) }
                offer.offer.provider?.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                    Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mb-2") }) {
                        svcs.forEach { s -> Badge(s.name, variant = BadgeVariant.Alternative, pill = true) }
                    }
                }
                P(attrs = { classes("text-xs", "text-body-subtle") }) { Text("Expires: ${offer.offer.endDate.take(10)}") }
            }
            Button(attrs = {
                classes(
                    "ml-3", "flex-shrink-0", "flex", "items-center", "justify-center",
                    "bg-brand-softer", "text-fg-brand", "rounded-neu-base", "transition-all",
                    "hover:shadow-neu-sm", "active:shadow-neu-inset"
                )
                style { property("padding", "8px"); property("border", "none"); property("cursor", "pointer") }
                onClick { onRemove() }
            }) { Text("⭐") }
        }
        offer.offer.provider?.let {
            Button(attrs = {
                classes("mt-3", "text-xs", "text-fg-brand", "font-medium", "hover:underline")
                style { property("background", "none"); property("border", "none"); property("cursor", "pointer") }
                onClick { onProviderClick() }
            }) { Text("By ${it.name} →") }
        }
    }
}
