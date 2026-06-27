package com.wadii.components

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.*

enum class MessageType { SUCCESS, WARNING, ERROR }

@Composable
fun SnakBar(
    message: String,
    messageType: MessageType = MessageType.SUCCESS,
    durationMs: Long = 4000L,
    onDismiss: () -> Unit = {}
) {
    if (message.isEmpty()) return

    var dismissed by remember(message) { mutableStateOf(false) }

    LaunchedEffect(message) {
        delay(durationMs)
        dismissed = true
        delay(350)
        onDismiss()
    }

    if (dismissed) return

    val icon = when (messageType) {
        MessageType.SUCCESS -> "✓"
        MessageType.WARNING -> "⚠"
        MessageType.ERROR   -> "✕"
    }
    val bg = when (messageType) {
        MessageType.SUCCESS -> "bg-green-50"
        MessageType.WARNING -> "bg-yellow-50"
        MessageType.ERROR   -> "bg-red-50"
    }
    val text = when (messageType) {
        MessageType.SUCCESS -> "text-green-700"
        MessageType.WARNING -> "text-yellow-700"
        MessageType.ERROR   -> "text-red-700"
    }
    val border = when (messageType) {
        MessageType.SUCCESS -> "border-green-200"
        MessageType.WARNING -> "border-yellow-200"
        MessageType.ERROR   -> "border-red-200"
    }

    // Outer div: only positioning (keeps transform: translateX(-50%) stable during animation)
    Div(attrs = {
        style {
            property("position", "fixed")
            property("bottom", "24px")
            property("left", "50%")
            property("transform", "translateX(-50%)")
            property("z-index", "9999")
            property("min-width", "300px")
            property("max-width", "480px")
            property("width", "max-content")
        }
    }) {
        // Inner div: visible card + slide-up animation
        Div(attrs = {
            classes(
                bg, "border", border,
                "rounded-2xl", "px-4", "py-3",
                "flex", "items-center", "gap-3",
                "shadow-lg", "snackbar-enter"
            )
        }) {
            Span(attrs = { classes("text-lg", "w-6", "text-center", "flex-shrink-0") }) {
                Text(icon)
            }
            Span(attrs = { classes("flex-1", "text-sm", "font-medium", text) }) {
                Text(message)
            }
            Button(attrs = {
                classes("flex-shrink-0", "opacity-40", "hover:opacity-100", "transition-opacity", text)
                style {
                    property("background", "none")
                    property("border", "none")
                    property("cursor", "pointer")
                    property("padding", "0")
                    property("font-size", "14px")
                    property("line-height", "1")
                }
                onClick { dismissed = true; onDismiss() }
            }) {
                Text("✕")
            }
        }
    }
}
