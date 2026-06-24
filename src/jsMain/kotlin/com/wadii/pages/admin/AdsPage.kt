package com.wadii.pages.admin

import androidx.compose.runtime.*
import com.wadii.api.*
import com.wadii.model.Advertisement
import com.wadii.state.AppState
import com.wadii.ui.InputField
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.Spinner
import com.wadii.ui.TextArea
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.dom.*

@Composable
fun AdsPage() {
    var ads by remember { mutableStateOf<List<Advertisement>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showModal by remember { mutableStateOf(false) }
    var editAd by remember { mutableStateOf<Advertisement?>(null) }
    val scope = rememberCoroutineScope()

    val reload: () -> Unit = { scope.launch { ads = apiGetAllAds(); loading = false } }
    LaunchedEffect(Unit) { reload() }

    Div(attrs = { classes("space-y-6") }) {
        Div(attrs = { classes("flex", "items-center", "justify-between") }) {
            H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Advertisements") }
            Button(attrs = {
                classes("flex", "items-center", "gap-2", "px-4", "py-2", "bg-amber-500",
                    "text-white", "rounded-xl", "text-sm", "font-medium", "hover:bg-amber-600")
                onClick { editAd = null; showModal = true }
            }) { Text("+ New Ad") }
        }

        if (loading) { LoadingSkeletons() }
        else if (ads.isEmpty()) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
                P(attrs = { classes("text-5xl", "mb-3") }) { Text("📣") }
                P(attrs = { classes("text-slate-500") }) { Text("No advertisements yet.") }
            }
        } else {
            Div(attrs = { classes("space-y-3") }) {
                ads.forEach { ad ->
                    Div(attrs = { classes("bg-white", "border", "border-slate-200", "rounded-xl",
                        "p-5", "flex", "items-center", "gap-4") }) {
                        ad.imageUrl?.let { img ->
                            Img(src = img, attrs = {
                                classes("w-16", "h-16", "rounded-lg", "object-cover", "flex-shrink-0")
                            })
                        }
                        Div(attrs = { classes("flex-1", "min-w-0") }) {
                            Div(attrs = { classes("flex", "items-center", "gap-2") }) {
                                P(attrs = { classes("font-semibold", "text-slate-800") }) { Text(ad.title) }
                                Span(attrs = { classes("text-xs", "px-2", "py-0.5", "rounded-full",
                                    if (ad.status == "ACTIVE") "bg-green-50 text-green-700" else "bg-slate-100 text-slate-500") }) {
                                    Text(ad.status)
                                }
                            }
                            P(attrs = { classes("text-sm", "text-slate-500", "truncate") }) { Text(ad.advertiserName) }
                            P(attrs = { classes("text-xs", "text-slate-400", "mt-1") }) {
                                Text("👁 ${ad.impressions}  🖱 ${ad.clicks}  ${ad.startDate.take(10)} – ${ad.endDate.take(10)}")
                            }
                        }
                        Div(attrs = { classes("flex", "gap-1") }) {
                            Button(attrs = {
                                classes("p-2", "text-slate-400", "hover:text-amber-500", "hover:bg-amber-50", "rounded-lg")
                                onClick { editAd = ad; showModal = true }
                            }) { Text("✏️") }
                            Button(attrs = {
                                classes("p-2", "text-slate-400", "hover:text-red-500", "hover:bg-red-50", "rounded-lg")
                                onClick {
                                    scope.launch {
                                        if (apiDeleteAd(ad.id)) { reload(); AppState.toast("Deleted") }
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

    if (showModal) {
        AdModal(editAd, onClose = { showModal = false }, onSaved = { reload(); showModal = false })
    }
}

@Composable
private fun AdModal(ad: Advertisement?, onClose: () -> Unit, onSaved: () -> Unit) {
    var title by remember { mutableStateOf(ad?.title ?: "") }
    var description by remember { mutableStateOf(ad?.description ?: "") }
    var imageUrl by remember { mutableStateOf(ad?.imageUrl ?: "") }
    var targetUrl by remember { mutableStateOf(ad?.targetUrl ?: "") }
    var advertiserName by remember { mutableStateOf(ad?.advertiserName ?: "") }
    var status by remember { mutableStateOf(ad?.status ?: "ACTIVE") }
    var startDate by remember { mutableStateOf(ad?.startDate?.take(10) ?: "") }
    var endDate by remember { mutableStateOf(ad?.endDate?.take(10) ?: "") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun save() {
        if (loading) return
        loading = true
        scope.launch {
            val body = buildMap<String, Any?> {
                put("title", title); put("description", description)
                put("imageUrl", imageUrl.ifBlank { null })
                put("targetUrl", targetUrl.ifBlank { null })
                put("advertiserName", advertiserName); put("status", status)
                put("startDate", startDate); put("endDate", endDate)
                ad?.let { put("id", it.id) }
            }
            val result = if (ad != null) apiUpdateAd(body) else apiInsertAd(body)
            loading = false
            if (result != null) { AppState.toast(if (ad != null) "Ad updated!" else "Ad created!"); onSaved() }
            else AppState.toast("Failed to save", true)
        }
    }

    Div(attrs = { classes("fixed", "inset-0", "z-50", "bg-black/50", "flex", "items-center",
        "justify-center", "p-4", "overflow-y-auto") }) {
        Div(attrs = { classes("bg-white", "rounded-2xl", "w-full", "max-w-lg", "p-6", "my-4") }) {
            H2(attrs = { classes("text-lg", "font-semibold", "text-slate-800", "mb-4") }) {
                Text(if (ad != null) "Edit Ad" else "New Advertisement")
            }
            Div(attrs = { classes("space-y-3") }) {
                Div(attrs = { classes("grid", "grid-cols-2", "gap-3") }) {
                    InputField("Title", title, required = true) { title = it }
                    InputField("Advertiser", advertiserName, required = true) { advertiserName = it }
                }
                TextArea("Description", description, rows = 2) { description = it }
                InputField("Image URL", imageUrl, "https://…") { imageUrl = it }
                InputField("Target URL", targetUrl, "https://…") { targetUrl = it }
                Div(attrs = { classes("grid", "grid-cols-3", "gap-3") }) {
                    Div {
                        P(attrs = { classes("text-sm", "font-medium", "text-slate-700", "mb-1") }) { Text("Status") }
                        val st = status
                        Select(attrs = {
                            classes("w-full", "px-3", "py-2", "border", "border-slate-300", "rounded-lg",
                                "text-sm", "focus:outline-none", "focus:ring-2", "focus:ring-amber-400")
                            onChange { status = it.value ?: "ACTIVE" }
                        }) {
                            Option("ACTIVE", attrs = { if (st == "ACTIVE") selected() }) { Text("Active") }
                            Option("INACTIVE", attrs = { if (st == "INACTIVE") selected() }) { Text("Inactive") }
                        }

                    }
                    InputField("Start Date", startDate, type = "date", required = true) { startDate = it }
                    InputField("End Date", endDate, type = "date", required = true) { endDate = it }
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
