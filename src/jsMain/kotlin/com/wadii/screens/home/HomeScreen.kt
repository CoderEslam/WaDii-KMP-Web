package com.wadii.screens.home

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.core.isNotNullOrEmptyString
import com.wadii.data.api.to1dp
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.service.Service
import com.wadii.pages.shared.chat.ChatScreen
import com.wadii.screens.orders.new.NewOrderScreen
import com.wadii.screens.providerDetail.ProviderDetailScreen
import com.wadii.screens.search.SearchScreen
import com.wadii.state.AppState
import com.wadii.ui.AnimatedVisibility
import com.wadii.ui.Avatar
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.classNames
import com.wadii.domain.usecase.MarketplaceUseCase
import com.wadii.utils.Validation
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import com.wadii.utils.greeting
import com.wadii.utils.isExpired
import com.wadii.ui.LoadingScreen
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.Modal
import com.wadii.ui.ModalVariant
import com.wadii.ui.PrimaryButton
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.*

private external fun encodeURIComponent(str: String): String

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val homeViewModel = navigator.koinNavigatorScreenModel<HomeViewModel>()
        val state by homeViewModel.state.collectAsState()
        val user = AppState.user

        // Count each ad once per visit it is on screen (POST /advertisements/track-impression/{id}).
        val scope = rememberCoroutineScope()
        val marketplace = remember { KoinPlatform.getKoin().get<MarketplaceUseCase>() }
        LaunchedEffect(state.ads.map { it.id }) {
            state.ads.forEach { ad -> marketplace.trackAdImpression(ad.id) { } }
        }

        Div(attrs = { classes("space-y-8") }) {
            Card(classes = "p-6 md:p-8 hero-card") {
                H1(attrs = { classes("text-2xl", "md:text-3xl", "font-semibold", "text-heading", "mb-2") }) {
                    Text("${greeting()}, ${user?.firstName ?: "there"} ")
                    Span(attrs = { classes("wave") }) { Text("👋") }
                }
                P(attrs = { classes("text-body", "mb-4") }) { Text("Need a part? Ask once and let sellers near you come to you with prices.") }
                Div(attrs = { classes("flex", "flex-wrap", "gap-3") }) {
                    PrimaryButton("Request a part", icon = "📦") { navigator.push(NewOrderScreen()) }
                    GhostButton("Browse sellers", icon = "🔍") { navigator.replaceAll(SearchScreen()) }
                }
            }

            if (state.services.isNotEmpty()) {
                Div {
                    P(attrs = { classes("text-lg", "font-semibold", "text-heading", "mb-3") }) { Text("⚙️ Categories") }
                    Div(attrs = { classes("flex", "flex-wrap", "gap-2") }) {
                        if (state.selectedService.name.isNotNullOrEmptyString()) {
                            Button(attrs = {
                                classes(
                                    "w-8", "h-8", "flex", "items-center", "justify-center",
                                    "rounded-full", "bg-surface", "text-fg-brand", "shadow-neu-sm",
                                    "hover:shadow-neu-md", "transition-all", "text-sm", "font-bold"
                                )
                                onClick { homeViewModel.onEvent(HomeEvent.SelectService(Service())) }
                            }) { Text("×") }
                        }
                        state.services.forEach { service ->
                            val active = state.selectedService.id == service.id
                            Button(attrs = {
                                classes(*classNames(
                                    "px-4", "py-2", "rounded-full", "text-sm", "font-medium", "border", "transition-all",
                                    if (active) "bg-surface shadow-neu-inset text-fg-brand border-brand-subtle"
                                    else "bg-surface shadow-neu-sm border-default text-body hover:shadow-neu-md hover:text-heading"
                                ))
                                onClick { homeViewModel.onEvent(HomeEvent.SelectService(service)) }
                            }) { Text(service.name) }
                        }
                    }
                }
            }

            if (state.ads.isNotEmpty()) {
                Div {
                    P(attrs = { classes("text-lg", "font-semibold", "text-heading", "mb-3") }) { Text("📣 Featured") }
                    Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-3", "gap-4") }) {
                        state.ads.forEach { ad ->
                            Div(attrs = {
                                style { property("cursor", "pointer") }
                                // Ads are admin-entered free text; never open anything but a plain http(s) link.
                                onClick {
                                    scope.launch { marketplace.trackAdClick(ad.id) { } }
                                    ad.targetUrl?.takeIf { Validation.url(it) == null }?.let { window.open(it, "_blank", "noopener,noreferrer") }
                                }
                            }) {
                                Card(classes = "overflow-hidden hover:shadow-neu-md transition-all") {
                                    ad.imageUrl?.let {
                                        Img(src = it, attrs = { classes("w-full", "h-32", "object-cover") })
                                    }
                                    Div(attrs = { classes("p-4") }) {
                                        Badge(ad.advertiserName, variant = BadgeVariant.Brand, pill = true)
                                        P(attrs = { classes("font-semibold", "mt-2", "text-heading") }) { Text(ad.title) }
                                        P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) { Text(ad.description) }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Div {
                P(attrs = { classes("text-lg", "font-semibold", "text-heading", "mb-3") }) { Text("🔩 Parts Sellers") }
                if (state.filterLoading) {
                    LoadingSkeletons(4, "h-28")
                } else if (state.filteredProviders.isEmpty()) {
                    EmptyState(
                        "🔩",
                        if (state.selectedService.name.isNotNullOrEmptyString()) "No sellers for this category." else "No sellers available yet."
                    )
                } else {
                    Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "lg:grid-cols-3", "gap-4", "stagger") }) {
                        state.filteredProviders.forEach { ProviderCard(navigator, it) }
                    }
                }
            }

            Div {
                P(attrs = { classes("text-lg", "font-semibold", "text-heading", "mb-4") }) { Text("🏷️ Latest Deals") }
                // Expired deals are hidden: buyers should never see a deal they can no longer use.
                val visible = state.offers.filter { !isExpired(it.endDate) }
                if (visible.isEmpty()) {
                    EmptyState(
                        "🏷️",
                        if (state.selectedService.name.isNotNullOrEmptyString()) "No deals for this category." else "No deals right now",
                        "Check back soon, sellers add new deals all the time."
                    )
                } else {
                    Div(attrs = { classes("grid", "grid-cols-1", "md:grid-cols-2", "lg:grid-cols-3", "gap-4", "stagger") }) {
                        visible.forEach { offer ->
                            OfferCard(
                                navigator = navigator,
                                offer = offer,
                                onSaveToggle = { homeViewModel.onEvent(HomeEvent.ToggleSaveOffer(offer)) }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(state.isLoading) {
                LoadingScreen()
            }
        }

        // Labelled so first-time users know what "+" does.
        Button(attrs = {
            classes(
                "fixed", "bottom-6", "right-6", "z-40",
                "h-14", "pl-4", "pr-5", "rounded-full", "bg-brand", "text-on-brand", "font-semibold", "shadow-neu-lg",
                "hover:shadow-neu-xl", "transition-all", "fab-pulse",
                "flex", "items-center", "justify-center", "gap-2"
            )
            style { property("border", "none"); property("cursor", "pointer") }
            attr("title", "Request a part")
            attr("aria-label", "Request a part")
            onClick { navigator.push(NewOrderScreen()) }
        }) {
            Span(attrs = { classes("text-2xl", "font-bold", "leading-none") }) { Text("+") }
            Span(attrs = { classes("text-sm") }) { Text("New order") }
        }
    }
}

@Composable
fun ProviderCard(navigator: Navigator, provider: ProviderModel) {
    var showCallDialog by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }

    Div(attrs = {
        classes("h-full")
        style { property("cursor", "pointer") }
        onClick { navigator.push(ProviderDetailScreen(provider.id)) }
    }) {
        Card(classes = "p-5 hover:shadow-neu-md transition-all h-full flex flex-col") {
            Div(attrs = { classes("flex", "items-center", "gap-4", "flex-1") }) {
                Avatar(initials = provider.name.take(1).uppercase())
                Div(attrs = { classes("flex-1", "min-w-0") }) {
                    P(attrs = { classes("font-semibold", "text-heading", "truncate") }) { Text(provider.name) }
                    Div(attrs = { classes("flex", "items-center", "gap-3", "mt-1") }) {
                        // No ratings yet is "New", not a discouraging 0.0.
                        if (provider.rate > 0.0) Span(attrs = { classes("text-sm", "text-warning") }) { Text("⭐ ${provider.rate.to1dp()}") }
                        else Badge("New", variant = BadgeVariant.Success, pill = true)
                        Span(attrs = { classes("text-xs", "text-body-subtle") }) { Text("${provider.followersCount} followers") }
                    }
                    provider.services?.takeIf { it.isNotEmpty() }?.let { svcs ->
                        Div(attrs = { classes("flex", "flex-wrap", "gap-1", "mt-2", "items-center") }) {
                            svcs.take(3).forEach { s -> Badge(s.name, variant = BadgeVariant.Alternative, pill = true) }
                            if (svcs.size > 3) Span(attrs = { classes("text-xs", "text-body-subtle") }) { Text("+${svcs.size - 3}") }
                        }
                    }
                }
            }

            Div(attrs = {
                classes("flex", "items-center", "gap-2", "mt-4", "pt-4", "border-t", "border-default")
                onClick { it.stopPropagation() }
            }) {
                // Chat only exists after the customer accepts a response, so the shortcut here is a direct quote request.
                if (provider.active && provider.acceptingOrders && provider.user.id != com.wadii.state.AppState.user?.id) {
                    ProviderQuickAction("📝", "Quote") {
                        navigator.push(NewOrderScreen(provider.id, provider.name, provider.services.map { it.id }))
                    }
                }
                ProviderQuickAction("📞", "Call") { showCallDialog = true }
                ProviderQuickAction("📍", "Address") { showAddressDialog = true }
            }
        }
    }

    Modal(
        open = showCallDialog,
        title = provider.name,
        onDismiss = { showCallDialog = false },
        variant = ModalVariant.PopUp,
        icon = "📞"
    ) {
        val phone = provider.user.phone
        if (phone.isNotEmpty()) {
            A(href = "tel:$phone", attrs = { classes("text-lg", "font-semibold", "text-fg-brand", "hover:underline") }) { Text(phone) }
        } else {
            P(attrs = { classes("text-body-subtle") }) { Text("No phone number available.") }
        }
    }

    Modal(
        open = showAddressDialog,
        title = "${provider.name} — Locations",
        onDismiss = { showAddressDialog = false }
    ) {
        if (provider.branches.isEmpty()) {
            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("No address available.") }
        } else {
            Div(attrs = { classes("space-y-4") }) {
                provider.branches.forEach { b ->
                    Div(attrs = { classes("flex", "items-start", "justify-between", "gap-3") }) {
                        Div {
                            P(attrs = { classes("font-medium", "text-heading") }) { Text(b.name) }
                            P(attrs = { classes("text-sm", "text-body-subtle", "mt-0.5") }) { Text(b.address) }
                        }
                        Button(attrs = {
                            classes("text-xs", "text-fg-brand", "font-medium", "hover:underline", "flex-shrink-0", "whitespace-nowrap")
                            style { property("background", "none"); property("border", "none"); property("cursor", "pointer") }
                            onClick { window.open("https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(b.address)}", "_blank") }
                        }) { Text("Open in Maps →") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderQuickAction(icon: String, label: String, onClick: () -> Unit) {
    Button(attrs = {
        classes(
            "flex-1", "flex", "items-center", "justify-center", "gap-1.5",
            "px-3", "py-2", "rounded-neu-default", "bg-surface", "text-body-subtle",
            "text-xs", "font-medium", "hover:shadow-neu-sm", "hover:text-fg-brand",
            "active:shadow-neu-inset", "transition-all"
        )
        style { property("border", "none"); property("cursor", "pointer") }
        onClick { onClick() }
    }) {
        Span(attrs = { classes("text-sm") }) { Text(icon) }
        Span { Text(label) }
    }
}

@Composable
fun OfferCard(
    navigator: Navigator,
    offer: OfferResponse,
    onSaveToggle: ((Boolean) -> Unit)? = null
) {
    Card(classes = "p-5 hover:shadow-neu-md transition-all") {
        Div(attrs = { classes("flex", "items-start", "justify-between") }) {
            Div(attrs = { classes("flex-1") }) {
                P(attrs = { classes("font-semibold", "text-heading", "mb-1") }) { Text(offer.title) }
                P(attrs = { classes("text-sm", "text-body-subtle", "mb-3") }) { Text(offer.description) }
                P(attrs = { classes("text-xs", "text-fg-disabled") }) { Text("Expires: ${offer.endDate.take(10)}") }
            }
            if (onSaveToggle != null) {
                Button(attrs = {
                    classes(*classNames(
                        "ml-3", "p-2", "rounded-neu-default", "bg-surface", "transition-all",
                        if (offer.saved) "text-fg-brand shadow-neu-inset"
                        else "text-body-subtle hover:text-fg-brand hover:shadow-neu-sm"
                    ))
                    style { property("border", "none"); property("cursor", "pointer") }
                    onClick { onSaveToggle(offer.saved) }
                }) { Text("⭐") }
            }
        }
        offer.provider?.let { p ->
            Button(attrs = {
                classes("mt-3", "text-xs", "text-fg-brand-strong", "font-medium", "hover:underline")
                style { property("background", "none"); property("border", "none"); property("cursor", "pointer") }
                onClick { navigator.push(ProviderDetailScreen(p.id)) }
            }) { Text("By ${p.name} →") }
        }
    }
}
