package com.wadii.pages.user

import androidx.compose.runtime.*
import com.wadii.api.to1dp
import com.wadii.model.Offer
import com.wadii.model.Provider
import com.wadii.screens.ProviderDetailScreen
import com.wadii.screens.SearchScreen
import com.wadii.state.AppState
import com.wadii.ui.LoadingScreen
import com.wadii.ui.LoadingSkeletons
import com.wadii.navigation.LocalNavigator
import com.wadii.navigation.Navigator
import com.wadii.navigation.currentOrThrow
import com.wadii.viewmodel.HomeEvent
import com.wadii.viewmodel.HomeScreenModel
import com.wadii.viewmodel.UiState
import com.wadii.viewmodel.rememberScreenModel
import org.jetbrains.compose.web.dom.*

@Composable
fun HomePage() {
    val navigator = LocalNavigator.currentOrThrow
    val model = rememberScreenModel { HomeScreenModel() }
    val user = AppState.user

    Div(attrs = { classes("space-y-8") }) {
        // Hero
        Div(attrs = { classes("bg-gradient-to-r", "from-amber-500", "to-orange-500", "rounded-2xl", "p-8", "text-white") }) {
            H1(attrs = { classes("text-3xl", "font-bold", "mb-2") }) { Text("Welcome back, ${user?.firstName}!") }
            P(attrs = { classes("text-amber-100", "mb-4") }) { Text("Find the best service providers near you") }
            Button(attrs = {
                classes("bg-white", "text-amber-600", "font-semibold", "px-6", "py-2.5", "rounded-lg", "hover:bg-amber-50", "transition-colors")
                onClick { navigator.replaceAll(SearchScreen) }
            }) { Text("Search Providers") }
        }

        when (val s = model.state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) { Text(s.message) }
            is UiState.Success -> {
                val d = s.data

                // Services filter
                if (d.services.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-lg", "font-semibold", "text-slate-800", "mb-3") }) { Text("🛠️ Our Services") }
                        Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                            d.services.forEach { service ->
                                val active = d.selectedServiceId == service.id
                                Button(attrs = {
                                    classes("px-4", "py-2", "rounded-full", "text-sm", "font-medium", "transition-colors")
                                    if (active) classes("bg-amber-500", "text-white", "border", "border-amber-500")
                                    else classes("bg-white", "border", "border-slate-200", "text-slate-700", "hover:border-amber-400", "hover:text-amber-600", "hover:bg-amber-50")
                                    onClick { model.onEvent(HomeEvent.SelectService(if (active) null else service.id)) }
                                }) { Text(service.name) }
                            }
                        }
                    }
                }

                // Ads
                if (d.ads.isNotEmpty()) {
                    Div {
                        P(attrs = { classes("text-lg", "font-semibold", "text-slate-800", "mb-3") }) { Text("📣 Featured") }
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-3", "gap-4") }) {
                            d.ads.forEach { ad ->
                                Div(attrs = {
                                    classes("bg-white", "border", "border-slate-200", "rounded-xl", "overflow-hidden", "hover:shadow-md", "transition-shadow", "cursor-pointer")
                                    onClick { ad.targetUrl?.let { kotlinx.browser.window.open(it, "_blank") } }
                                }) {
                                    ad.imageUrl?.let { Img(src = it, attrs = { classes("w-full", "h-32", "object-cover") }) }
                                    Div(attrs = { classes("p-4") }) {
                                        Span(attrs = { classes("text-xs", "font-medium", "text-amber-600", "bg-amber-50", "px-2", "py-0.5", "rounded-full") }) { Text(ad.advertiserName) }
                                        P(attrs = { classes("font-semibold", "mt-2", "text-slate-800") }) { Text(ad.title) }
                                        P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(ad.description) }
                                    }
                                }
                            }
                        }
                    }
                }

                // Providers
                Div {
                    P(attrs = { classes("text-lg", "font-semibold", "text-slate-800", "mb-3") }) { Text("🔧 Service Providers") }
                    if (d.filterLoading) {
                        LoadingSkeletons(4, "h-28")
                    } else if (d.filteredProviders.isEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-xl", "p-8", "text-center", "text-slate-500", "text-sm") }) {
                            Text(if (d.selectedServiceId != null) "No providers for this service." else "No providers available yet.")
                        }
                    } else {
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "lg:grid-cols-3", "gap-4") }) {
                            d.filteredProviders.forEach { ProviderCard(navigator, it) }
                        }
                    }
                }

                // Offers
                Div {
                    P(attrs = { classes("text-lg", "font-semibold", "text-slate-800", "mb-4") }) { Text("🏷️ Latest Offers") }
                    val visible = d.visibleOffers
                    if (visible.isEmpty()) {
                        Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) {
                            Text(if (d.selectedServiceId != null) "No offers for this service." else "No offers available yet.")
                        }
                    } else {
                        Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "lg:grid-cols-3", "gap-4") }) {
                            visible.forEach { offer ->
                                OfferCard(navigator = navigator, offer = offer, onSaveToggle = { model.onEvent(HomeEvent.ToggleSaveOffer(offer)) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderCard(navigator: Navigator, provider: Provider) {
    Div(attrs = {
        classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5", "hover:shadow-md", "transition-shadow", "cursor-pointer")
        onClick { navigator.push(ProviderDetailScreen(provider.id)) }
    }) {
        Div(attrs = { classes("flex", "items-center", "gap-4") }) {
            Div(attrs = { classes("w-12", "h-12", "rounded-full", "bg-amber-100", "flex", "items-center", "justify-center", "flex-shrink-0") }) {
                Span(attrs = { classes("text-xl", "font-bold", "text-amber-600") }) { Text(provider.name.take(1).uppercase()) }
            }
            Div(attrs = { classes("flex-1", "min-w-0") }) {
                P(attrs = { classes("font-semibold", "text-slate-800", "truncate") }) { Text(provider.name) }
                Div(attrs = { classes("flex", "items-center", "gap-3", "mt-1") }) {
                    Span(attrs = { classes("text-sm", "text-amber-500") }) { Text("⭐ ${provider.rate.to1dp()}") }
                    Span(attrs = { classes("text-xs", "text-slate-400") }) { Text("${provider.followersCount} followers") }
                }
                provider.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                    Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2") }) {
                        svcs.take(3).forEach { s ->
                            Span(attrs = { classes("text-xs", "bg-slate-100", "text-slate-600", "px-2", "py-0.5", "rounded-full") }) { Text(s.name) }
                        }
                        if (svcs.size > 3) Span(attrs = { classes("text-xs", "text-slate-400") }) { Text("+${svcs.size - 3}") }
                    }
                }
            }
        }
    }
}

@Composable
fun OfferCard(navigator: Navigator, offer: Offer, onSaveToggle: ((Boolean) -> Unit)? = null) {
    Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5", "hover:shadow-md", "transition-shadow") }) {
        Div(attrs = { classes("flex", "items-start", "justify-between") }) {
            Div(attrs = { classes("flex-1") }) {
                P(attrs = { classes("font-semibold", "text-slate-800", "mb-1") }) { Text(offer.title) }
                P(attrs = { classes("text-sm", "text-slate-500", "mb-3") }) { Text(offer.description) }
                offer.services?.takeIf { it.isNotEmpty() }?.let { services ->
                    Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mb-2") }) {
                        services.forEach { s ->
                            Span(attrs = { classes("text-xs", "bg-slate-100", "text-slate-600", "px-2", "py-0.5", "rounded-full") }) { Text(s.name) }
                        }
                    }
                }
                P(attrs = { classes("text-xs", "text-slate-400") }) { Text("Expires: ${offer.endDate.take(10)}") }
            }
            if (onSaveToggle != null) {
                Button(attrs = {
                    val saveClasses = if (offer.saved) arrayOf("text-amber-500", "bg-amber-50")
                                     else arrayOf("text-slate-400", "hover:text-amber-500", "hover:bg-amber-50")
                    classes("ml-3", "p-2", "rounded-lg", "transition-colors", *saveClasses)
                    onClick { onSaveToggle(offer.saved) }
                }) { Text("⭐") }
            }
        }
        offer.provider?.let { p ->
            Button(attrs = {
                classes("mt-3", "text-xs", "text-amber-600", "font-medium", "hover:underline")
                onClick { navigator.push(ProviderDetailScreen(p.id)) }
            }) { Text("By ${p.name} →") }
        }
    }
}
