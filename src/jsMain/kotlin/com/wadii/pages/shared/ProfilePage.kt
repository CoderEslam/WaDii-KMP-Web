package com.wadii.pages.shared

import androidx.compose.runtime.*
import com.wadii.api.apiGetMe
import com.wadii.api.apiUploadImage
import com.wadii.api.to1dp
import com.wadii.model.User
import com.wadii.state.AppState
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.Spinner
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList

@Composable
fun ProfilePage() {
    var user by remember { mutableStateOf<User?>(AppState.user) }
    var loading by remember { mutableStateOf(user == null) }
    var uploadingAvatar by remember { mutableStateOf(false) }
    var uploadingBg by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (user == null) {
            user = apiGetMe()
            loading = false
        }
    }

    fun pickAndUpload(onUploaded: (String) -> Unit, setUploading: (Boolean) -> Unit) {
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.accept = "image/*"
        input.onchange = {
            val file = input.files?.asList()?.firstOrNull()
            if (file != null) {
                setUploading(true)
                scope.launch {
                    val uploaded = apiUploadImage(file)
                    setUploading(false)
                    if (uploaded != null) onUploaded(uploaded)
                    else AppState.toast("Upload failed", true)
                }
            }
            null
        }
        input.click()
    }

    Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text("Profile") }

        if (loading) { LoadingSkeletons(3) }
        else if (user == null) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center", "text-slate-400") }) {
                Text("Unable to load profile.")
            }
        } else {
            val u = user!!

            // Cover + Avatar
            Div(attrs = { classes("bg-white", "rounded-2xl", "shadow-sm", "overflow-hidden") }) {
                Div(attrs = { classes("relative", "h-40", "bg-gradient-to-r",
                    "from-amber-400", "to-amber-600") }) {
                    u.backgroundImage?.let { bg ->
                        Img(src = "http://192.168.1.30:8080/uploads/$bg", attrs = {
                            classes("absolute", "inset-0", "w-full", "h-full", "object-cover")
                        })
                    }
                    Button(attrs = {
                        classes("absolute", "bottom-2", "right-2", "px-2.5", "py-1",
                            "bg-black/30", "text-white", "text-xs", "rounded-lg",
                            "hover:bg-black/50", "backdrop-blur-sm", "disabled:opacity-60",
                            "flex", "items-center", "gap-1")
                        onClick {
                            pickAndUpload(
                                onUploaded = { uploaded -> user = u.copy(backgroundImage = uploaded); AppState.toast("Background updated!") },
                                setUploading = { uploadingBg = it }
                            )
                        }
                        if (uploadingBg) disabled()
                    }) {
                        if (uploadingBg) Spinner() else Text("📷 Change cover")
                    }
                }

                Div(attrs = { classes("px-6", "pb-6") }) {
                    Div(attrs = { classes("flex", "items-end", "gap-4", "-mt-10", "mb-4") }) {
                        Div(attrs = { classes("relative") }) {
                            Div(attrs = { classes("w-20", "h-20", "rounded-full", "border-4",
                                "border-white", "bg-amber-100", "overflow-hidden", "shadow") }) {
                                u.image?.let { img ->
                                    Img(src = "http://192.168.1.30:8080/uploads/$img", attrs = {
                                        classes("w-full", "h-full", "object-cover")
                                    })
                                } ?: Div(attrs = { classes("w-full", "h-full", "flex",
                                    "items-center", "justify-center") }) {
                                    Span(attrs = { classes("text-2xl", "font-bold", "text-amber-600") }) {
                                        Text(u.firstName.take(1).uppercase())
                                    }
                                }
                            }
                            Button(attrs = {
                                classes("absolute", "-bottom-1", "-right-1", "w-6", "h-6",
                                    "bg-amber-500", "text-white", "rounded-full", "text-xs",
                                    "flex", "items-center", "justify-center", "hover:bg-amber-600",
                                    "disabled:opacity-60", "shadow")
                                onClick {
                                    pickAndUpload(
                                        onUploaded = { uploaded -> user = u.copy(image = uploaded); AppState.toast("Avatar updated!") },
                                        setUploading = { uploadingAvatar = it }
                                    )
                                }
                                if (uploadingAvatar) disabled()
                            }) { if (uploadingAvatar) Spinner() else Text("✏️") }
                        }
                    }

                    Div(attrs = { classes("space-y-1") }) {
                        H2(attrs = { classes("text-xl", "font-bold", "text-slate-800") }) {
                            Text("${u.firstName} ${u.lastName}")
                        }
                        P(attrs = { classes("text-slate-500") }) { Text(u.email) }
                        u.phone?.let { ph ->
                            P(attrs = { classes("text-slate-500", "text-sm") }) { Text("📞 $ph") }
                        }
                        Span(attrs = { classes("inline-block", "mt-2", "px-3", "py-1", "rounded-full",
                            "text-xs", "font-medium",
                            when (u.role) {
                                "ADMIN" -> "bg-purple-50 text-purple-700"
                                "PROVIDER" -> "bg-blue-50 text-blue-700"
                                else -> "bg-green-50 text-green-700"
                            }) }) {
                            Text(u.role)
                        }
                        u.city?.let { city ->
                            P(attrs = { classes("text-sm", "text-slate-400", "mt-1") }) {
                                Text("📍 ${city.name}")
                                city.province?.let { Text(", ${it.name}") }
                                city.province?.country?.let { Text(", ${it.name}") }
                            }
                        }
                    }
                }
            }

            // Provider info
            u.provider?.let { prov ->
                Div(attrs = { classes("bg-white", "rounded-2xl", "shadow-sm", "p-6") }) {
                    H2(attrs = { classes("font-semibold", "text-slate-800", "mb-4") }) { Text("Business Info") }
                    Div(attrs = { classes("space-y-3") }) {
                        InfoRow("Business Name", prov.name)
                        InfoRow("Rating", "⭐ ${prov.rate.to1dp()}")
                        InfoRow("Followers", prov.followersCount.toString())
                    }
                }
            }

            // Logout
            Div(attrs = { classes("bg-white", "rounded-2xl", "shadow-sm", "p-6") }) {
                Button(attrs = {
                    classes("w-full", "py-3", "border-2", "border-red-200", "text-red-600",
                        "font-semibold", "rounded-xl", "hover:bg-red-50", "transition-colors")
                    onClick { AppState.logout() }
                }) { Text("Sign Out") }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Div(attrs = { classes("flex", "items-center", "justify-between", "py-2",
        "border-b", "border-slate-50") }) {
        P(attrs = { classes("text-sm", "text-slate-500") }) { Text(label) }
        P(attrs = { classes("text-sm", "font-medium", "text-slate-800") }) { Text(value) }
    }
}
