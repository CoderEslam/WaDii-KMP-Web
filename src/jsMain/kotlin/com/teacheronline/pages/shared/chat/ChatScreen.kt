package com.teacheronline.pages.shared.chat

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.teacheronline.core.isNotNullOrEmptyString
import com.teacheronline.domain.model.call.CallChannel
import com.teacheronline.domain.model.chat.ChatContact
import com.teacheronline.pages.shared.call.CallScreen
import com.teacheronline.state.AppState
import com.teacheronline.ui.AnimatedVisibility
import com.teacheronline.ui.Avatar
import com.teacheronline.ui.LoadingScreen
import com.teacheronline.ui.classNames
import com.teacheronline.ui.PrimaryButton
import com.teacheronline.ui.SecondaryButton
import com.teacheronline.ui.Spinner
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement

class ChatScreen(private val initialContact: ChatContact? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<ChatViewModel>()
        val state by model.state.collectAsState()
        val myId = AppState.user?.id

        LaunchedEffect(initialContact?.contact?.id) {
            initialContact?.let { model.onEvent(ChatEvent.SelectContact(it)) }
        }

        Div(attrs = {
            classes(
                "flex",
                "bg-surface",
                "border",
                "border-default",
                "rounded-neu-base",
                "shadow-neu-lg",
                "overflow-hidden"
            )
            style { property("height", "calc(100vh - 8rem)") }
        }) {
            AnimatedVisibility(state.isLoading) {
                LoadingScreen()
            }
            AnimatedVisibility(state.error.isNotNullOrEmptyString()) {
                Div(attrs = {
                    classes(
                        "flex-1",
                        "flex",
                        "items-center",
                        "justify-center",
                        "text-body-subtle"
                    )
                }) { Text(state.error) }
            }
            Div(attrs = {
                classes(
                    "w-80",
                    "border-r",
                    "border-default",
                    "flex",
                    "flex-col",
                    "flex-shrink-0"
                )
            }) {
                Div(attrs = { classes("p-4", "border-b", "border-default") }) {
                    H2(attrs = { classes("font-semibold", "text-heading") }) { Text("Chats") }
                }
                Div(attrs = { classes("flex-1", "overflow-y-auto") }) {
                    if (state.contacts.isEmpty()) {
                        Div(attrs = { classes("p-8", "text-center") }) {
                            P(attrs = { classes("text-3xl", "mb-2") }) { Text("💬") }
                            P(attrs = {
                                classes(
                                    "text-body-subtle",
                                    "text-sm"
                                )
                            }) { Text("No conversations yet.") }
                        }
                    } else {
                        state.contacts.forEach { chatContact ->
                            val displayName =
                                chatContact.contact.fullName.ifBlank { null } ?: "Unknown"
                            val isSelected = state.selectedContact.id == chatContact.id
                            Div(attrs = {
                                classes(
                                    *classNames(
                                        "flex",
                                        "items-center",
                                        "gap-3",
                                        "px-4",
                                        "py-3",
                                        "transition-all",
                                        if (isSelected) "bg-surface shadow-neu-inset" else "bg-surface hover:shadow-neu-sm"
                                    )
                                )
                                style { property("cursor", "pointer") }
                                onClick { model.onEvent(ChatEvent.SelectContact(chatContact)) }
                            }) {
                                Avatar(
                                    imageUrl = null,
                                    initials = displayName.take(1).uppercase()
                                )
                                Div(attrs = { classes("flex-1", "min-w-0") }) {
                                    P(attrs = {
                                        classes(
                                            "font-medium",
                                            "text-heading",
                                            "truncate"
                                        )
                                    }) { Text(displayName) }
                                    P(attrs = {
                                        classes(
                                            "text-xs",
                                            "text-body-subtle",
                                            "truncate"
                                        )
                                    }) { Text(chatContact.lastMessage) }
                                }
                            }
                        }
                    }
                }
            }

            Div(attrs = { classes("flex-1", "flex", "flex-col") }) {
                if (state.selectedContact == ChatContact()) {
                    Div(attrs = {
                        classes(
                            "flex-1",
                            "flex",
                            "items-center",
                            "justify-center",
                            "text-center",
                            "text-fg-disabled"
                        )
                    }) {
                        Div {
                            P(attrs = { classes("text-6xl", "mb-3") }) { Text("💬") }
                            P(attrs = {
                                classes(
                                    "text-lg",
                                    "font-medium"
                                )
                            }) { Text("Select a conversation") }
                        }
                    }
                } else {
                    val contact = state.selectedContact
                    val displayName = contact.contact.fullName.ifBlank { null } ?: "Unknown"
                    Div(attrs = {
                        classes(
                            "px-5",
                            "py-4",
                            "border-b",
                            "border-default",
                            "flex",
                            "items-center",
                            "gap-3"
                        )
                    }) {
                        Avatar(
                            imageUrl = null,
                            initials = displayName.take(1).uppercase(),
                            size = com.teacheronline.ui.AvatarSize.SM
                        )
                        P(attrs = {
                            classes(
                                "font-semibold",
                                "text-heading"
                            )
                        }) { Text(displayName) }
                        Div(attrs = { classes("ml-auto", "flex", "gap-2") }) {
                            SecondaryButton("Call", icon = "📞") {
                                val myId = AppState.user?.id ?: return@SecondaryButton
                                navigator.push(
                                    CallScreen(
                                        channelName = CallChannel.name(myId, contact.contact.id),
                                        remoteUserId = contact.contact.id,
                                        remoteUserName = displayName,
                                        remoteUserImage = null,
                                        withVideo = false,
                                        isCaller = true
                                    )
                                )
                            }
                            PrimaryButton("Video", icon = "🎥") {
                                val myId = AppState.user?.id ?: return@PrimaryButton
                                navigator.push(
                                    CallScreen(
                                        channelName = CallChannel.name(myId, contact.contact.id),
                                        remoteUserId = contact.contact.id,
                                        remoteUserName = displayName,
                                        remoteUserImage = null,
                                        withVideo = true,
                                        isCaller = true
                                    )
                                )
                            }
                        }
                    }
                    var messagesContainer by remember { mutableStateOf<HTMLDivElement?>(null) }
                    var scrollHeightBeforeLoad by remember { mutableStateOf(0.0) }
                    LaunchedEffect(state.isLoadingMoreMessages) {
                        if (state.isLoadingMoreMessages) {
                            scrollHeightBeforeLoad =
                                messagesContainer?.scrollHeight?.toDouble() ?: 0.0
                        }
                    }
                    LaunchedEffect(state.messages.size, state.selectedContact.id) {
                        messagesContainer?.let {
                            if (state.isLoadingMoreMessages) {
                                val diff = it.scrollHeight - scrollHeightBeforeLoad
                                it.scrollTop = it.scrollTop + diff
                            } else {
                                it.scrollTop = it.scrollHeight.toDouble()
                            }
                        }
                    }
                    Div(attrs = {
                        classes("flex-1", "overflow-y-auto", "p-5", "space-y-3")
                        ref { element ->
                            messagesContainer = element
                            val onScroll: (org.w3c.dom.events.Event) -> Unit = {
                                if (element.scrollTop < 80.0) {
                                    model.onEvent(ChatEvent.LoadMoreMessages)
                                }
                            }
                            element.addEventListener("scroll", onScroll)
                            onDispose {
                                element.removeEventListener("scroll", onScroll)
                                messagesContainer = null
                            }
                        }
                    }) {
                        if (state.messagesLoading) {
                            Div(attrs = { classes("flex", "justify-center", "py-8") }) { Spinner() }
                        } else if (state.messages.isEmpty()) {
                            Div(attrs = {
                                classes(
                                    "flex",
                                    "items-center",
                                    "justify-center",
                                    "h-full",
                                    "text-fg-disabled",
                                    "text-sm"
                                )
                            }) { Text("No messages yet. Say hello!") }
                        } else {
                            state.messages.forEach { msg ->
                                val isMe = msg.fromUser.id == myId
                                Div(attrs = {
                                    classes(
                                        "flex",
                                        if (isMe) "justify-end" else "justify-start"
                                    )
                                }) {
                                    Div(attrs = {
                                        classes(
                                            *classNames(
                                                "max-w-xs",
                                                "px-4",
                                                "py-2",
                                                "rounded-neu-base",
                                                "text-sm",
                                                if (isMe) "bg-brand text-on-brand" else "bg-surface-secondary text-heading"
                                            )
                                        )
                                    }) {
                                        Text(msg.text)
                                        P(attrs = {
                                            classes(
                                                "text-xs",
                                                "mt-1",
                                                if (isMe) "text-white/70" else "text-body-subtle"
                                            )
                                        }) {
                                            Text(msg.createdAt.take(16).replace("T", " "))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Div(attrs = { classes("px-5", "py-4", "border-t", "border-default") }) {
                        Div(attrs = { classes("flex", "gap-3") }) {
                            Input(type = InputType.Text, attrs = {
                                classes(
                                    "flex-1",
                                    "px-4",
                                    "py-2.5",
                                    "border",
                                    "border-default-medium",
                                    "rounded-neu-base",
                                    "bg-surface",
                                    "shadow-neu-inset",
                                    "text-sm",
                                    "text-heading",
                                    "focus:outline-none",
                                    "focus:ring-1",
                                    "focus:ring-brand",
                                    "focus:border-brand"
                                )
                                attr("placeholder", "Type a message…")
                                value(state.messageText)
                                onInput { model.onEvent(ChatEvent.SetMessage(it.value)) }
                                onKeyDown { e ->
                                    if (e.key == "Enter" && !e.shiftKey) {
                                        e.preventDefault(); model.onEvent(ChatEvent.Send)
                                    }
                                }
                            })
                            PrimaryButton(
                                "Send",
                                loading = state.sending,
                                disabled = state.messageText.isBlank()
                            ) {
                                model.onEvent(ChatEvent.Send)
                            }
                        }
                    }
                }
            }
        }
    }
}
