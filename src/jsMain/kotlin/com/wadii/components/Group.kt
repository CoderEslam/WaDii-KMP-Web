package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

/** White glass card that groups related content with an optional section title. */
@Composable
fun Group(
    title: String = "",
    content: @Composable () -> Unit
) {
    Div(attrs = { classes("bg-white", "rounded-2xl", "overflow-hidden") }) {
        if (title.isNotEmpty()) {
            Div(attrs = { classes("px-5", "pt-4", "pb-2") }) {
                P(attrs = { classes("text-xs", "font-semibold", "text-slate-500", "uppercase", "tracking-widest") }) {
                    Text(title)
                }
            }
        }
        content()
    }
}

/** A single row item inside a Group — icon, label, optional trailing content. */
@Composable
fun GroupItem(
    icon: String? = null,
    label: String,
    trailing: @Composable (() -> Unit)? = null,
    divider: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Div(attrs = {
        classes(
            "flex", "items-center", "gap-3", "px-5", "py-3",
            if (onClick != null) "cursor-pointer hover:bg-slate-50 transition-colors" else ""
        )
        onClick?.let { onClick { it() } }
    }) {
        icon?.let {
            Span(attrs = { classes("text-lg", "w-6", "text-center", "flex-shrink-0") }) { Text(it) }
        }
        Span(attrs = { classes("flex-1", "text-sm", "text-slate-700") }) { Text(label) }
        trailing?.invoke()
    }
    if (divider) {
        Div(attrs = { classes("border-b", "border-slate-100", "mx-5") }) {}
    }
}
