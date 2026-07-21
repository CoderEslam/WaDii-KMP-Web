package com.teacheronline.pages.shared.profile

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.teacheronline.domain.model.auth.Role
import com.teacheronline.state.AppState
import com.teacheronline.ui.Avatar
import com.teacheronline.ui.AvatarSize
import com.teacheronline.ui.Badge
import com.teacheronline.ui.BadgeVariant
import com.teacheronline.ui.Card
import com.teacheronline.ui.DangerButton
import org.jetbrains.compose.web.dom.*

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<ProfileViewModel>()
        val state by model.state.collectAsState()
        val user = state.user

        Div(attrs = { classes("max-w-2xl", "mx-auto", "space-y-6") }) {
            H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("Profile") }

            Card(classes = "p-6") {
                Div(attrs = { classes("flex", "items-center", "gap-4", "mb-4") }) {
                    Avatar(initials = user.firstName.take(1).uppercase(), size = AvatarSize.XXL)
                    Div(attrs = { classes("space-y-1") }) {
                        H2(attrs = { classes("text-xl", "font-semibold", "text-heading") }) { Text(user.fullName) }
                        P(attrs = { classes("text-body-subtle") }) { Text(user.email) }
                        val roleVariant = when (user.role) {
                            Role.ADMIN -> BadgeVariant.Danger
                            Role.TEACHER -> BadgeVariant.Brand
                            Role.SECRETARY -> BadgeVariant.Warning
                            else -> BadgeVariant.Success
                        }
                        Badge(user.role.name, variant = roleVariant, pill = true)
                    }
                }
                if (user.educationalCenters.isNotEmpty()) {
                    Div(attrs = { classes("space-y-2", "mt-4") }) {
                        P(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text("Educational Centers") }
                        user.educationalCenters.forEach { center ->
                            P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("${center.name} — ${center.address}") }
                        }
                    }
                }
            }

            Card(classes = "p-6") {
                DangerButton("Sign Out", fullWidth = true) { AppState.logout() }
            }
        }
    }
}
