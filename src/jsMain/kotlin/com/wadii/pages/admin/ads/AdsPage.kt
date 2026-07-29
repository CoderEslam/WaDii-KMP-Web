package com.wadii.pages.admin.ads

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.EmptyState
import com.wadii.ui.GhostButton
import com.wadii.ui.LoadingScreen
import com.wadii.ui.PageHeader
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf


class AdsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val adsScreenModel = koinScreenModel<AdsScreenModel> { parametersOf(null) }
        val state by adsScreenModel.state.collectAsState()
        Div(attrs = { classes("space-y-6") }) {
            PageHeader("Advertisements", "+ New Ad") { navigator.push(CreateAdScreen()) }
            if (state.isLoading) {
                LoadingScreen()
            }
            if (state.ads.isEmpty()) {
                EmptyState("📣", "No advertisements yet.")
            } else {
                Div(attrs = { classes("space-y-3") }) {
                    state.ads.forEach { ad ->
                        Card(classes = "p-5 flex items-center gap-4") {
                            ad.imageUrl?.let { img ->
                                Img(
                                    src = img,
                                    attrs = {
                                        classes(
                                            "w-16",
                                            "h-16",
                                            "rounded-neu-base",
                                            "object-cover",
                                            "flex-shrink-0"
                                        )
                                    })
                            }
                            Div(attrs = { classes("flex-1", "min-w-0") }) {
                                Div(attrs = { classes("flex", "items-center", "gap-2") }) {
                                    P(attrs = { classes("font-semibold", "text-heading") }) {
                                        Text(
                                            ad.title
                                        )
                                    }
                                    Badge(
                                        ad.status,
                                        variant = if (ad.status == "ACTIVE") BadgeVariant.Success else BadgeVariant.Gray,
                                        pill = true
                                    )
                                }
                                P(attrs = {
                                    classes(
                                        "text-sm",
                                        "text-body-subtle",
                                        "truncate"
                                    )
                                }) { Text(ad.advertiserName) }
                                P(attrs = { classes("text-xs", "text-body-subtle", "mt-1") }) {
                                    Text(
                                        "👁 ${ad.impressions}  🖱 ${ad.clicks}  ${
                                            ad.startDate.take(
                                                10
                                            )
                                        } – ${ad.endDate.take(10)}"
                                    )
                                }
                            }
                            Div(attrs = { classes("flex", "gap-1") }) {
                                GhostButton("✏️") { navigator.push(CreateAdScreen(ad)) }
                                GhostButton("🗑️") { adsScreenModel.onEvent(AdsEvent.Delete(ad.id)) }
                            }
                        }
                    }
                }
            }
        }
    }
}
