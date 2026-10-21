package com.wadii.pages.admin.users

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.state.AppState
import com.wadii.ui.Alert
import com.wadii.ui.AlertVariant
import com.wadii.ui.Badge
import com.wadii.ui.BadgeVariant
import com.wadii.ui.Card
import com.wadii.ui.DangerButton
import com.wadii.ui.EmptyState
import com.wadii.ui.InputField
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.Modal
import com.wadii.ui.ModalVariant
import com.wadii.ui.SecondaryButton
import org.jetbrains.compose.web.dom.*

class UsersScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<UsersViewModel>()
        val state by model.state.collectAsState()
        val myId = AppState.user?.id

        Div(attrs = { classes("space-y-6") }) {
            Div {
                H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text("👥 Users") }
                P(attrs = { classes("text-sm", "text-body-subtle", "mt-1") }) {
                    Text("Every account on the platform. Switching a role keeps the person's data.")
                }
            }

            when {
                state.isLoading -> LoadingSkeletons(5, "h-20")
                state.error != null -> Alert(variant = AlertVariant.Danger, body = state.error!!)
                state.users.isEmpty() -> EmptyState("👥", "No users yet")
                else -> {
                    InputField("Search", state.query, "Name, email or phone", leadingIcon = "🔍") {
                        model.onEvent(UsersEvent.SetQuery(it))
                    }
                    val q = state.query.trim()
                    val visible = state.users.filter {
                        q.isEmpty() || "${it.firstName} ${it.lastName}".contains(q, true) ||
                            it.email.contains(q, true) || it.phone.contains(q, true)
                    }
                    if (visible.isEmpty()) {
                        EmptyState("🔎", "No users match")
                    } else {
                        Div(attrs = { classes("space-y-3", "stagger") }) {
                            visible.forEach { user ->
                                val busy = state.busyUserId == user.id
                                Card(classes = "p-4") {
                                    Div(attrs = { classes("flex", "flex-col", "sm:flex-row", "sm:items-center", "justify-between", "gap-3") }) {
                                        Div(attrs = { classes("min-w-0") }) {
                                            Div(attrs = { classes("flex", "items-center", "gap-2", "flex-wrap") }) {
                                                P(attrs = { classes("font-medium", "text-heading") }) {
                                                    Text("${user.firstName} ${user.lastName}".trim().ifBlank { user.email })
                                                }
                                                Badge(
                                                    user.role.lowercase().replaceFirstChar { it.uppercase() },
                                                    variant = when (user.role) {
                                                        "ADMIN" -> BadgeVariant.Danger
                                                        "PROVIDER" -> BadgeVariant.Brand
                                                        else -> BadgeVariant.Alternative
                                                    },
                                                    pill = true
                                                )
                                                if (user.id == myId) Badge("You", variant = BadgeVariant.Success, pill = true)
                                            }
                                            P(attrs = { classes("text-sm", "text-body-subtle", "break-words") }) { Text(user.email) }
                                            if (user.phone.isNotBlank()) P(attrs = { classes("text-sm", "text-body-subtle") }) { Text("📞 ${user.phone}") }
                                        }
                                        // Admins and the signed-in admin themself are not editable from here.
                                        if (user.role != "ADMIN" && user.id != myId) {
                                            Div(attrs = { classes("flex", "gap-2", "flex-shrink-0") }) {
                                                if (user.role == "PROVIDER") {
                                                    SecondaryButton("Make buyer", disabled = busy) { model.onEvent(UsersEvent.MakeBuyer(user)) }
                                                } else {
                                                    SecondaryButton("Make seller", disabled = busy) { model.onEvent(UsersEvent.MakeSeller(user)) }
                                                }
                                                DangerButton("Delete", disabled = busy) { model.onEvent(UsersEvent.AskDelete(user)) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Modal(
            open = state.deletingUser != null,
            title = "Delete ${state.deletingUser?.firstName ?: "this account"}?",
            onDismiss = { model.onEvent(UsersEvent.DismissDelete) },
            variant = ModalVariant.Form,
            footer = {
                SecondaryButton("Keep it") { model.onEvent(UsersEvent.DismissDelete) }
                DangerButton("Delete account", loading = state.busyUserId != null, onClick = { model.onEvent(UsersEvent.ConfirmDelete) })
            }
        ) {
            P(attrs = { classes("text-sm", "text-body") }) {
                Text("${state.deletingUser?.email.orEmpty()} loses access for good. If the account still has orders or a shop, the server will refuse and nothing changes.")
            }
        }
    }
}
