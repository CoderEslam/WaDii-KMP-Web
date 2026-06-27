package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search…",
    onNotificationClick: (() -> Unit)? = null,
    onOrdersClick: (() -> Unit)? = null,
    notificationCount: Int = 0
) {
    Div(attrs = { classes("flex", "items-center", "gap-3", "px-1", "py-2") }) {
        // Search pill
        Div(attrs = {
            classes(
                "flex", "items-center", "gap-2", "flex-1",
                "bg-white", "border", "border-slate-200", "rounded-full",
                "px-4", "py-2.5", "focus-within:ring-2", "focus-within:ring-amber-400",
                "focus-within:border-transparent", "transition-all"
            )
        }) {
            Span(attrs = { classes("text-slate-400", "flex-shrink-0") }) { Text("⊹") }
            Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
                classes("flex-1", "outline-none", "bg-transparent", "text-sm", "text-slate-800", "placeholder-slate-400")
                attr("placeholder", placeholder)
                attr("value", value)
                onInput { onValueChange(it.value) }
            })
            if (value.isNotEmpty()) {
                Button(attrs = {
                    classes("text-slate-400", "hover:text-slate-600", "bg-transparent", "border-none", "cursor-pointer", "p-0", "text-sm")
                    onClick { onValueChange("") }
                }) { Text("✕") }
            }
        }

        // Icon buttons
        if (onOrdersClick != null) {
            SearchIconButton(icon = "◈", label = "Orders", onClick = onOrdersClick)
        }
        if (onNotificationClick != null) {
            SearchIconButton(
                icon = "◇",
                label = "Notifications",
                badge = notificationCount,
                onClick = onNotificationClick
            )
        }
    }
}

@Composable
private fun SearchIconButton(
    icon: String,
    label: String,
    badge: Int = 0,
    onClick: () -> Unit
) {
    Div(attrs = { classes("relative", "flex-shrink-0") }) {
        Button(attrs = {
            classes(
                "w-10", "h-10", "rounded-full", "bg-white", "border", "border-slate-200",
                "flex", "items-center", "justify-center", "text-slate-600",
                "hover:border-amber-400", "hover:text-amber-600", "transition-colors",
                "cursor-pointer"
            )
            attr("title", label)
            onClick { onClick() }
        }) { Text(icon) }

        if (badge > 0) {
            Span(attrs = {
                classes(
                    "absolute", "-top-1", "-right-1",
                    "w-4", "h-4", "bg-amber-500", "text-white",
                    "rounded-full", "text-xs", "flex", "items-center", "justify-center", "font-bold"
                )
            }) { Text(if (badge > 99) "99+" else badge.toString()) }
        }
    }
}
