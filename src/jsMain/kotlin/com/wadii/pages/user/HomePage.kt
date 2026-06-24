package com.wadii.pages.user

import androidx.compose.runtime.*
import com.wadii.api.*
import com.wadii.model.Advertisement
import com.wadii.model.Offer
import com.wadii.router.Route
import com.wadii.state.AppState
import com.wadii.ui.LoadingSkeletons
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.*

@Composable
fun HomePage() {
    var offers by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var ads by remember { mutableStateOf<List<Advertisement>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val user = AppState.user

    LaunchedEffect(Unit) {
        offers = apiGetAllOffers().take(6)
        ads = apiGetActiveAds().take(3)
        loading = false
    }

    Div(attrs = { classes("space-y-8") }) {
        // Hero
        Div(attrs = { classes("bg-gradient-to-r", "from-amber-500", "to-orange-500",
            "rounded-2xl", "p-8", "text-white") }) {
            H1(attrs = { classes("text-3xl", "font-bold", "mb-2") }) {
                Text("Welcome back, ${user?.firstName}!")
            }
            P(attrs = { classes("text-amber-100", "mb-4") }) {
                Text("Find the best service providers near you")
            }
            Button(attrs = {
                classes("bg-white", "text-amber-600", "font-semibold", "px-6", "py-2.5",
                    "rounded-lg", "hover:bg-amber-50", "transition-colors")
                onClick { AppState.navigate(Route.SEARCH) }
            }) { Text("Search Providers") }
        }

        // Ads
        if (ads.isNotEmpty()) {
            Div {
                P(attrs = { classes("text-lg", "font-semibold", "text-slate-800", "mb-3") }) {
                    Text("📣 Featured")
                }
                Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-3", "gap-4") }) {
                    ads.forEach { ad ->
                        Div(attrs = {
                            classes("bg-white", "border", "border-slate-200", "rounded-xl",
                                "overflow-hidden", "hover:shadow-md", "transition-shadow", "cursor-pointer")
                            onClick {
                                scope.launch { apiTrackClick(ad.id) }
                                ad.targetUrl?.let { kotlinx.browser.window.open(it, "_blank") }
                            }
                        }) {
                            if (ad.imageUrl != null) {
                                Img(src = ad.imageUrl, attrs = {
                                    classes("w-full", "h-32", "object-cover")
                                })
                            }
                            Div(attrs = { classes("p-4") }) {
                                Span(attrs = { classes("text-xs", "font-medium", "text-amber-600",
                                    "bg-amber-50", "px-2", "py-0.5", "rounded-full") }) {
                                    Text(ad.advertiserName)
                                }
                                P(attrs = { classes("font-semibold", "mt-2", "text-slate-800") }) { Text(ad.title) }
                                P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(ad.description) }
                            }
                        }
                    }
                }
            }
        }

        // Offers
        Div {
            Div(attrs = { classes("flex", "items-center", "justify-between", "mb-4") }) {
                P(attrs = { classes("text-lg", "font-semibold", "text-slate-800") }) {
                    Text("🏷️ Latest Offers")
                }
            }
            if (loading) {
                LoadingSkeletons(6, "h-40")
            } else if (offers.isEmpty()) {
                Div(attrs = { classes("bg-white", "rounded-xl", "p-12", "text-center", "text-slate-500") }) {
                    Text("No offers available yet.")
                }
            } else {
                Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "lg:grid-cols-3", "gap-4") }) {
                    offers.forEach { offer ->
                        OfferCard(offer, onSaveToggle = { saved ->
                            scope.launch {
                                if (saved) apiRemoveSavedOffer(offer.id) else apiSaveOffer(offer.id)
                                offers = apiGetAllOffers().take(6)
                                AppState.toast(if (saved) "Offer removed" else "Offer saved!")
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun OfferCard(offer: Offer, onSaveToggle: ((Boolean) -> Unit)? = null) {
    Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl", "p-5",
        "hover:shadow-md", "transition-shadow") }) {
        Div(attrs = { classes("flex", "items-start", "justify-between") }) {
            Div(attrs = { classes("flex-1") }) {
                P(attrs = { classes("font-semibold", "text-slate-800", "mb-1") }) { Text(offer.title) }
                P(attrs = { classes("text-sm", "text-slate-500", "mb-3") }) { Text(offer.description) }
                offer.services?.takeIf { it.isNotEmpty() }?.let { services ->
                    Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mb-2") }) {
                        services.forEach { s ->
                            Span(attrs = { classes("text-xs", "bg-slate-100", "text-slate-600",
                                "px-2", "py-0.5", "rounded-full") }) { Text(s.name) }
                        }
                    }
                }
                P(attrs = { classes("text-xs", "text-slate-400") }) {
                    Text("Expires: ${offer.endDate.take(10)}")
                }
            }
            if (onSaveToggle != null) {
                Button(attrs = {
                    classes("ml-3", "p-2", "rounded-lg", "transition-colors",
                        if (offer.saved) "text-amber-500 bg-amber-50" else "text-slate-400 hover:text-amber-500 hover:bg-amber-50")
                    onClick { onSaveToggle(offer.saved) }
                }) { Text("⭐") }
            }
        }
        offer.provider?.let { p ->
            Button(attrs = {
                classes("mt-3", "text-xs", "text-amber-600", "font-medium", "hover:underline")
                onClick { AppState.navigate(Route.PROVIDER_DETAIL, p.id.toString()) }
            }) { Text("By ${p.name} →") }
        }
    }
}
