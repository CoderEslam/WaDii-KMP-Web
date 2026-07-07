package com.wadii.pages.admin.ads

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.domain.model.ads.Ads
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.BackButton
import com.wadii.ui.Card
import com.wadii.ui.InputField
import com.wadii.ui.PrimaryButton
import com.wadii.ui.TextArea
import org.jetbrains.compose.web.dom.*
import org.koin.core.parameter.parametersOf

class CreateAdScreen(private val existingAd: Ads? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<AdsScreenModel> { parametersOf(existingAd) }
        val state by model.state.collectAsState()
        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            Div(attrs = { classes("flex", "items-center", "gap-3") }) {
                BackButton { navigator.pop() }
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) {
                    Text(if (state.isEdit) "Edit Ad" else "New Ad")
                }
            }

            Card(classes = "p-6") {
                Div(attrs = { classes("space-y-5") }) {
                    if (state.error != null) {
                        Alert(variant = AlertVariant.Danger, body = state.error!!)
                    }

                    InputField("Title", state.title, "Summer promo", required = true) {
                        model.onEvent(AdsEvent.SetTitle(it))
                    }
                    InputField(
                        "Advertiser Name",
                        state.advertiserName,
                        "Acme Corp",
                        required = true
                    ) {
                        model.onEvent(AdsEvent.SetAdvertiserName(it))
                    }
                    TextArea("Description", state.description, "What is this ad about…") {
                        model.onEvent(AdsEvent.SetDescription(it))
                    }
                    InputField("Image URL", state.imageUrl, "https://…", type = "url") {
                        model.onEvent(AdsEvent.SetImageUrl(it))
                    }
                    InputField("Target URL", state.targetUrl, "https://…", type = "url") {
                        model.onEvent(AdsEvent.SetTargetUrl(it))
                    }

                    Div(attrs = { classes("grid", "grid-cols-2", "gap-4") }) {
                        InputField("Start Date", state.startDate, type = "date") {
                            model.onEvent(AdsEvent.SetStartDate(it))
                        }
                        InputField("End Date", state.endDate, type = "date") {
                            model.onEvent(AdsEvent.SetEndDate(it))
                        }
                    }

                    InputField("Priority", state.priority, "0", type = "number") {
                        model.onEvent(AdsEvent.SetPriority(it))
                    }

                    PrimaryButton(
                        if (state.isEdit) "Save Changes" else "Create Ad",
                        loading = state.isLoading,
                        fullWidth = true
                    ) {
                        model.onEvent(AdsEvent.Submit)
                    }
                }
            }
        }
    }
}
