package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun CheckBox(
    label: String,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Label(attrs = {
        classes("inline-flex", "items-center", "gap-3", "cursor-pointer", "select-none", "group")
    }) {
        Div(attrs = {
            classes(
                "w-5", "h-5", "rounded", "border-2", "flex", "items-center", "justify-center",
                "transition-all", "flex-shrink-0",
                if (checked) "bg-amber-500 border-amber-500" else "border-slate-300 bg-white group-hover:border-amber-400"
            )
            onClick { onCheckedChange(!checked) }
        }) {
            if (checked) {
                Span(attrs = {
                    classes("text-white")
                    style { property("font-size", "11px"); property("line-height", "1") }
                }) { Text("✓") }
            }
        }
        Span(attrs = { classes("text-sm", "text-slate-700") }) { Text(label) }
    }
}
