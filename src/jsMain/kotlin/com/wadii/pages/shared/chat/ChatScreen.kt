package com.wadii.pages.shared.chat

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wadii.core.isNotNullOrEmptyString
import com.wadii.domain.model.chat.ChatContact
import com.wadii.state.AppState
import com.wadii.ui.AnimatedVisibility
import com.wadii.ui.LoadingScreen
import com.wadii.ui.Spinner
import com.wadii.utils.Constants
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement

class ChatScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<ChatViewModel>()
        val state by model.state.collectAsState()
        val myId = AppState.user?.id

        Div(attrs = {
            classes(
                "flex",
                "h-[calc(100vh-8rem)]",
                "bg-white",
                "rounded-2xl",
                "shadow-sm",
                "overflow-hidden"
            )
        }) {
            AnimatedVisibility(state.isLoading){
                LoadingScreen()
            }
            AnimatedVisibility(state.error.isNotNullOrEmptyString()){
                Div(attrs = {
                    classes(
                        "flex-1",
                        "flex",
                        "items-center",
                        "justify-center",
                        "text-slate-500"
                    )
                }) { Text(state.error) }
            }
            Div(attrs = {
                classes(
                    "w-80",
                    "border-r",
                    "border-slate-100",
                    "flex",
                    "flex-col",
                    "flex-shrink-0"
                )
            }) {
                Div(attrs = { classes("p-4", "border-b", "border-slate-100") }) {
                    H2(attrs = {
                        classes(
                            "font-semibold",
                            "text-slate-800"
                        )
                    }) { Text("Chats") }
                }
                Div(attrs = { classes("flex-1", "overflow-y-auto") }) {
                    if (state.contacts.isEmpty()) {
                        Div(attrs = { classes("p-8", "text-center") }) {
                            P(attrs = { classes("text-3xl", "mb-2") }) { Text("💬") }
                            P(attrs = {
                                classes(
                                    "text-slate-500",
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
                                    "flex",
                                    "items-center",
                                    "gap-3",
                                    "px-4",
                                    "py-3",
                                    "cursor-pointer",
                                    "hover:bg-slate-50",
                                    "transition-colors"
                                )
                                if (isSelected) classes("bg-amber-50") else classes()
                                onClick { model.onEvent(ChatEvent.SelectContact(chatContact)) }
                            }) {
                                Div(attrs = {
                                    classes(
                                        "w-10",
                                        "h-10",
                                        "rounded-full",
                                        "bg-amber-100",
                                        "flex",
                                        "items-center",
                                        "justify-center",
                                        "flex-shrink-0",
                                        "overflow-hidden"
                                    )
                                }) {
                                    chatContact.contact.image?.let { img ->
                                        Img(
                                            src = "${Constants.BASE_URL_USER_IMAGES}/$img",
                                            attrs = {
                                                classes(
                                                    "w-full",
                                                    "h-full",
                                                    "object-cover"
                                                )
                                            })
                                    } ?: Span(attrs = {
                                        classes(
                                            "text-amber-600",
                                            "font-medium"
                                        )
                                    }) { Text(displayName.take(1).uppercase()) }
                                }
                                Div(attrs = { classes("flex-1", "min-w-0") }) {
                                    P(attrs = {
                                        classes(
                                            "font-medium",
                                            "text-slate-800",
                                            "truncate"
                                        )
                                    }) { Text(displayName) }
                                    chatContact.lastMessage.let {
                                        P(attrs = {
                                            classes(
                                                "text-xs",
                                                "text-slate-400",
                                                "truncate"
                                            )
                                        }) { Text(it) }
                                    }
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
                            "text-slate-400"
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
                    val displayName =
                        contact.contact.fullName.ifBlank { null } ?: "Unknown"
                    Div(attrs = {
                        classes(
                            "px-5",
                            "py-4",
                            "border-b",
                            "border-slate-100",
                            "flex",
                            "items-center",
                            "gap-3"
                        )
                    }) {
                        Div(attrs = {
                            classes(
                                "w-9",
                                "h-9",
                                "rounded-full",
                                "bg-amber-100",
                                "flex",
                                "items-center",
                                "justify-center",
                                "overflow-hidden"
                            )
                        }) {
                            contact.contact?.image?.let {
                                Img(
                                    src = "${Constants.BASE_URL_USER_IMAGES}/$it",
                                    attrs = { classes("w-full", "h-full", "object-cover") })
                            }
                                ?: Span(attrs = {
                                    classes(
                                        "text-amber-600",
                                        "font-medium",
                                        "text-sm"
                                    )
                                }) { Text(displayName.take(1).uppercase()) }
                        }
                        P(attrs = { classes("font-semibold", "text-slate-800") }) {
                            Text(
                                displayName
                            )
                        }
                    }
                    var messagesContainer by remember { mutableStateOf<HTMLDivElement?>(null) }
                    LaunchedEffect(state.messages.size, state.selectedContact.id) {
                        messagesContainer?.let { it.scrollTop = it.scrollHeight.toDouble() }
                    }
                    Div(attrs = {
                        classes(
                            "flex-1",
                            "overflow-y-auto",
                            "p-5",
                            "space-y-3"
                        )
                        ref { element ->
                            messagesContainer = element
                            onDispose { messagesContainer = null }
                        }
                    }) {
                        if (state.messagesLoading) {
                            Div(attrs = {
                                classes(
                                    "flex",
                                    "justify-center",
                                    "py-8"
                                )
                            }) { Spinner() }
                        } else if (state.messages.isEmpty()) {
                            Div(attrs = {
                                classes(
                                    "flex",
                                    "items-center",
                                    "justify-center",
                                    "h-full",
                                    "text-slate-400",
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
                                            "max-w-xs",
                                            "px-4",
                                            "py-2",
                                            "rounded-2xl",
                                            "text-sm"
                                        )
                                        if (isMe) classes(
                                            "bg-amber-500",
                                            "text-white",
                                            "rounded-br-sm"
                                        )
                                        else classes(
                                            "bg-slate-100",
                                            "text-slate-800",
                                            "rounded-bl-sm"
                                        )
                                    }) {
                                        Text(msg.text)
                                        P(attrs = {
                                            classes(
                                                "text-xs",
                                                "mt-1",
                                                if (isMe) "text-amber-100" else "text-slate-400"
                                            )
                                        }) {
                                            Text(msg.createdAt.take(16).replace("T", " "))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Div(attrs = {
                        classes(
                            "px-5",
                            "py-4",
                            "border-t",
                            "border-slate-100"
                        )
                    }) {
                        Div(attrs = { classes("flex", "gap-3") }) {
                            Input(type = InputType.Text, attrs = {
                                classes(
                                    "flex-1",
                                    "px-4",
                                    "py-2.5",
                                    "border",
                                    "border-slate-300",
                                    "rounded-xl",
                                    "text-sm",
                                    "focus:outline-none",
                                    "focus:ring-2",
                                    "focus:ring-amber-400"
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
                            Button(attrs = {
                                classes(
                                    "px-4",
                                    "py-2.5",
                                    "bg-amber-500",
                                    "text-white",
                                    "rounded-xl",
                                    "text-sm",
                                    "font-medium",
                                    "hover:bg-amber-600",
                                    "disabled:opacity-60",
                                    "flex",
                                    "items-center",
                                    "gap-2"
                                )
                                onClick { model.onEvent(ChatEvent.Send) }
                                if (state.sending || state.messageText.isBlank()) disabled()
                            }) { if (state.sending) Spinner() else Text("Send") }
                        }
                    }
                }
            }
        }
    }
}
