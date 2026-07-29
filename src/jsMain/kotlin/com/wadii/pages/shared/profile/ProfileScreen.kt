package com.wadii.pages.shared.profile

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import com.wadii.data.api.to1dp
import com.wadii.state.AppState
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Avatar
import com.wadii.ui.AvatarSize
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.DangerButton
import com.wadii.ui.LoadingScreen
import com.wadii.ui.SecondaryButton
import com.wadii.ui.Spinner
import com.wadii.utils.Constants.BASE_URL_USER_IMAGES
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.domain.model.auth.Role
import com.wadii.domain.model.auth.login.User
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<ProfileViewModel>()
        val state by model.state.collectAsState()
        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Profile") }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> Alert(
                    variant = AlertVariant.Danger,
                    body = state.error ?: ""
                )
            }
            fun pickFile(onFile: (org.w3c.files.File) -> Unit) {
                val input = document.createElement("input") as HTMLInputElement
                input.type = "file"
                input.accept = "image/*"
                input.onchange = { input.files?.asList()?.firstOrNull()?.let(onFile); null }
                input.click()
            }
            Card(classes = "overflow-hidden") {
                Div(attrs = { classes("relative", "h-40", "bg-surface-secondary") }) {
                    state.user.backgroundImage?.let { bg ->
                        Img(
                            src = "$BASE_URL_USER_IMAGES/$bg",
                            attrs = {
                                classes(
                                    "absolute",
                                    "inset-0",
                                    "w-full",
                                    "h-full",
                                    "object-cover"
                                )
                            })
                    }
                    Button(attrs = {
                        classes(
                            "absolute",
                            "bottom-2",
                            "right-2",
                            "px-2.5",
                            "py-1",
                            "bg-surface",
                            "text-body",
                            "text-xs",
                            "rounded-neu-default",
                            "shadow-neu-sm",
                            "hover:shadow-neu-md",
                            "active:shadow-neu-inset",
                            "transition-all",
                            "disabled:opacity-60",
                            "flex",
                            "items-center",
                            "gap-1"
                        )
                        style { property("border", "none"); property("cursor", "pointer") }
                        onClick {
                            pickFile { file ->
                                model.onEvent(
                                    ProfileEvent.UploadBackground(
                                        file
                                    )
                                )
                            }
                        }
                        if (state.uploadingBg) disabled()
                    }) { if (state.uploadingBg) Spinner() else Text("📷 Change cover") }
                }
                Div(attrs = { classes("px-6", "pb-6") }) {
                    Div(attrs = {
                        classes(
                            "flex",
                            "items-end",
                            "gap-4",
                            "-mt-10",
                            "mb-4"
                        )
                    }) {
                        Div(attrs = { classes("relative") }) {
                            Avatar(
                                imageUrl = state.user.image?.let { "$BASE_URL_USER_IMAGES/$it" },
                                initials = state.user.firstName.take(1).uppercase(),
                                size = AvatarSize.XXL,
                                bordered = true
                            )
                            Button(attrs = {
                                classes(
                                    "absolute",
                                    "-bottom-1",
                                    "-right-1",
                                    "bg-surface",
                                    "text-fg-brand",
                                    "rounded-full",
                                    "text-xs",
                                    "flex",
                                    "items-center",
                                    "justify-center",
                                    "shadow-neu-sm",
                                    "hover:shadow-neu-md",
                                    "active:shadow-neu-inset",
                                    "transition-all",
                                    "disabled:opacity-60"
                                )
                                style {
                                    property("width", "24px"); property(
                                    "height",
                                    "24px"
                                ); property("border", "none"); property("cursor", "pointer")
                                }
                                onClick {
                                    pickFile { file ->
                                        model.onEvent(
                                            ProfileEvent.UploadAvatar(
                                                file
                                            )
                                        )
                                    }
                                }
                                if (state.uploadingAvatar) disabled()
                            }) { if (state.uploadingAvatar) Spinner() else Text("✏️") }
                        }
                    }
                    Div(attrs = { classes("space-y-1") }) {
                        H2(attrs = {
                            classes(
                                "text-xl",
                                "font-semibold",
                                "text-heading"
                            )
                        }) { Text("${state.user.firstName} ${state.user.lastName}") }
                        P(attrs = { classes("text-body-subtle") }) { Text(state.user.email) }
                        state.user.phone?.let { ph ->
                            P(attrs = {
                                classes(
                                    "text-body-subtle",
                                    "text-sm"
                                )
                            }) { Text("📞 $ph") }
                        }
                        val roleVariant = when (state.user.role) {
                            Role.ADMIN.name -> BadgeVariant.Danger
                            Role.PROVIDER.name -> BadgeVariant.Brand
                            else -> BadgeVariant.Success
                        }
                        Div(attrs = { classes("mt-2") }) {
                            Badge(
                                state.user.role,
                                variant = roleVariant,
                                pill = true
                            )
                        }
                        state.user.city.let { city ->
                            P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) {
                                Text("📍 ${city.name}")
                                city.province.let { Text(", ${it.name}") }
                                city.province.country?.let { Text(", ${it.name}") }
                            }
                        }
                    }
                }
            }
            state.user.provider?.let { prov ->
                Card(classes = "p-6") {
                    H2(attrs = { classes("font-semibold", "text-heading", "mb-4") }) {
                        Text(
                            "Seller Info"
                        )
                    }
                    Div(attrs = { classes("space-y-3") }) {
                        ProfileInfoRow("Shop Name", prov.name)
                        ProfileInfoRow("Rating", "⭐ ${prov.rate.to1dp()}")
                        ProfileInfoRow("Followers", prov.followersCount.toString())
                    }
                }
            }
            Card(classes = "p-6") {
                Div(attrs = { classes("space-y-3") }) {
                    SecondaryButton("Edit Profile", fullWidth = true) {
                        navigator.push(
                            EditProfileScreen()
                        )
                    }
                    DangerButton("Sign Out", fullWidth = true) { AppState.logout() }
                }
            }
            if (state.user.role != Role.ADMIN.name) {
                Div(attrs = {
                    style {
                        property("position", "fixed")
                        property("bottom", "24px")
                        property("right", "24px")
                        property("z-index", "50")
                    }
                }) {
                    Button(attrs = {
                        classes(
                            "flex",
                            "items-center",
                            "justify-center",
                            "gap-2",
                            "px-5",
                            "py-3",
                            "bg-surface",
                            "text-fg-brand",
                            "font-medium",
                            "rounded-full",
                            "shadow-neu-sm",
                            "hover:shadow-neu-md",
                            "active:shadow-neu-inset",
                            "transition-all",
                            "disabled:opacity-60"
                        )
                        style { property("border", "none"); property("cursor", "pointer") }
                        onClick {
                            if (state.user.role == "PROVIDER" || state.user.provider != null && state.user.provider?.id != 0L) {
                                model.onEvent(ProfileEvent.SwitchRole)
                            } else {
                                navigator.push(RequestProviderScreen())
                            }
                        }
                        if (state.switchingRole) disabled()
                    }) {
                        if (state.switchingRole) Spinner() else {
                            Text(if (state.user.role == Role.PROVIDER.name) "🧑 Switch to Buyer" else "🏬 Switch to Seller")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Div(attrs = {
        classes(
            "flex",
            "items-center",
            "justify-between",
            "py-2",
            "border-b",
            "border-light"
        )
    }) {
        P(attrs = { classes("text-sm", "text-body-subtle") }) { Text(label) }
        P(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text(value) }
    }
}
