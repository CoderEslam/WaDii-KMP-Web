package com.wadii.pages.shared

import androidx.compose.runtime.*
import com.wadii.api.apiGetChatList
import com.wadii.api.apiGetConversation
import com.wadii.api.apiSendMessage
import com.wadii.model.ChatContact
import com.wadii.model.Message
import com.wadii.state.AppState
import com.wadii.ui.LoadingSkeletons
import com.wadii.ui.Spinner
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*

@Composable
fun ChatPage() {
    var contacts by remember { mutableStateOf<List<ChatContact>>(emptyList()) }
    var selectedContact by remember { mutableStateOf<ChatContact?>(null) }
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var loadingContacts by remember { mutableStateOf(true) }
    var loadingMessages by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val myId = AppState.user?.id

    LaunchedEffect(Unit) {
        contacts = apiGetChatList()
        loadingContacts = false
    }

    LaunchedEffect(selectedContact) {
        val contact = selectedContact ?: return@LaunchedEffect
        loadingMessages = true
        messages = apiGetConversation(contact.id)
        loadingMessages = false
    }

    fun sendMessage() {
        val contact = selectedContact ?: return
        if (messageText.isBlank() || sending) return
        val text = messageText.trim()
        messageText = ""
        sending = true
        scope.launch {
            apiSendMessage(contact.id, text)
            messages = apiGetConversation(contact.id)
            sending = false
        }
    }

    Div(attrs = { classes("flex", "h-[calc(100vh-8rem)]", "bg-white", "rounded-2xl",
        "shadow-sm", "overflow-hidden") }) {

        // Contacts sidebar
        Div(attrs = { classes("w-80", "border-r", "border-slate-100", "flex", "flex-col", "flex-shrink-0") }) {
            Div(attrs = { classes("p-4", "border-b", "border-slate-100") }) {
                H2(attrs = { classes("font-semibold", "text-slate-800") }) { Text("Chats") }
            }
            Div(attrs = { classes("flex-1", "overflow-y-auto") }) {
                if (loadingContacts) {
                    Div(attrs = { classes("p-4") }) { LoadingSkeletons(4) }
                } else if (contacts.isEmpty()) {
                    Div(attrs = { classes("p-8", "text-center") }) {
                        P(attrs = { classes("text-3xl", "mb-2") }) { Text("💬") }
                        P(attrs = { classes("text-slate-500", "text-sm") }) { Text("No conversations yet.") }
                    }
                } else {
                    contacts.forEach { chatContact ->
                        val contactUser = chatContact.contact
                        val displayName = contactUser?.fullName?.ifBlank { null } ?: "Unknown"
                        val isSelected = selectedContact?.id == chatContact.id
                        Div(attrs = {
                            classes("flex", "items-center", "gap-3", "px-4", "py-3",
                                "cursor-pointer", "hover:bg-slate-50", "transition-colors",
                                if (isSelected) "bg-amber-50" else "")
                            onClick { selectedContact = chatContact }
                        }) {
                            Div(attrs = { classes("w-10", "h-10", "rounded-full", "bg-amber-100",
                                "flex", "items-center", "justify-center", "flex-shrink-0",
                                "overflow-hidden") }) {
                                contactUser?.image?.let { img ->
                                    Img(src = "http://192.168.1.30:8080/uploads/$img", attrs = {
                                        classes("w-full", "h-full", "object-cover")
                                    })
                                } ?: Span(attrs = { classes("text-amber-600", "font-medium") }) {
                                    Text(displayName.take(1).uppercase())
                                }
                            }
                            Div(attrs = { classes("flex-1", "min-w-0") }) {
                                P(attrs = { classes("font-medium", "text-slate-800", "truncate") }) {
                                    Text(displayName)
                                }
                                chatContact.lastMessage?.let { last ->
                                    P(attrs = { classes("text-xs", "text-slate-400", "truncate") }) {
                                        Text(last)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Chat area
        Div(attrs = { classes("flex-1", "flex", "flex-col") }) {
            if (selectedContact == null) {
                Div(attrs = { classes("flex-1", "flex", "items-center", "justify-center",
                    "text-center", "text-slate-400") }) {
                    Div {
                        P(attrs = { classes("text-6xl", "mb-3") }) { Text("💬") }
                        P(attrs = { classes("text-lg", "font-medium") }) { Text("Select a conversation") }
                        P(attrs = { classes("text-sm", "mt-1") }) { Text("Choose a contact from the list to start chatting.") }
                    }
                }
            } else {
                val contact = selectedContact!!
                val contactUser = contact.contact
                val displayName = contactUser?.fullName?.ifBlank { null } ?: "Unknown"

                // Header
                Div(attrs = { classes("px-5", "py-4", "border-b", "border-slate-100",
                    "flex", "items-center", "gap-3") }) {
                    Div(attrs = { classes("w-9", "h-9", "rounded-full", "bg-amber-100",
                        "flex", "items-center", "justify-center", "overflow-hidden") }) {
                        contactUser?.image?.let { img ->
                            Img(src = "http://192.168.1.30:8080/uploads/$img", attrs = {
                                classes("w-full", "h-full", "object-cover")
                            })
                        } ?: Span(attrs = { classes("text-amber-600", "font-medium", "text-sm") }) {
                            Text(displayName.take(1).uppercase())
                        }
                    }
                    P(attrs = { classes("font-semibold", "text-slate-800") }) { Text(displayName) }
                }

                // Messages
                Div(attrs = { classes("flex-1", "overflow-y-auto", "p-5", "space-y-3") }) {
                    if (loadingMessages) {
                        LoadingSkeletons(4)
                    } else if (messages.isEmpty()) {
                        Div(attrs = { classes("flex", "items-center", "justify-center", "h-full",
                            "text-slate-400", "text-sm") }) {
                            Text("No messages yet. Say hello!")
                        }
                    } else {
                        messages.forEach { msg ->
                            val isMe = msg.fromUser?.id == myId
                            Div(attrs = { classes("flex", if (isMe) "justify-end" else "justify-start") }) {
                                Div(attrs = {
                                    classes("max-w-xs", "px-4", "py-2", "rounded-2xl", "text-sm",
                                        if (isMe) "bg-amber-500 text-white rounded-br-sm"
                                        else "bg-slate-100 text-slate-800 rounded-bl-sm")
                                }) {
                                    Text(msg.text)
                                    P(attrs = {
                                        classes("text-xs", "mt-1",
                                            if (isMe) "text-amber-100" else "text-slate-400")
                                    }) {
                                        Text(msg.createdAt.take(16).replace("T", " "))
                                    }
                                }
                            }
                        }
                    }
                }

                // Input
                Div(attrs = { classes("px-5", "py-4", "border-t", "border-slate-100") }) {
                    Div(attrs = { classes("flex", "gap-3") }) {
                        Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
                            classes("flex-1", "px-4", "py-2.5", "border", "border-slate-300",
                                "rounded-xl", "text-sm", "focus:outline-none",
                                "focus:ring-2", "focus:ring-amber-400")
                            attr("placeholder", "Type a message…")
                            value(messageText)
                            onInput { messageText = it.value }
                            onKeyDown { e -> if (e.key == "Enter" && !e.shiftKey) { e.preventDefault(); sendMessage() } }
                        })
                        Button(attrs = {
                            classes("px-4", "py-2.5", "bg-amber-500", "text-white", "rounded-xl",
                                "text-sm", "font-medium", "hover:bg-amber-600", "disabled:opacity-60",
                                "flex", "items-center", "gap-2")
                            onClick { sendMessage() }
                            if (sending || messageText.isBlank()) disabled()
                        }) { if (sending) Spinner() else Text("Send") }
                    }
                }
            }
        }
    }
}
