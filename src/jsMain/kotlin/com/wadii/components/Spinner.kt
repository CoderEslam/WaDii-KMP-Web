package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun <T> Spinner(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    placeholder: String = "Select…",
    itemToString: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = selectedItem?.let { itemToString(it) } ?: placeholder

    Div(attrs = { classes("relative", "w-full") }) {
        // Trigger button
        Button(attrs = {
            classes(
                "w-full", "flex", "items-center", "justify-between", "gap-2",
                "px-4", "py-2.5", "rounded-xl", "border", "bg-white",
                "text-sm", "text-left", "cursor-pointer", "transition-all",
                if (expanded) "border-amber-400 ring-2 ring-amber-400" else "border-slate-300 hover:border-slate-400"
            )
            onClick { expanded = !expanded }
        }) {
            Span(attrs = {
                classes(if (selectedItem == null) "text-slate-400" else "text-slate-800", "flex-1", "truncate")
            }) { Text(displayText) }
            Span(attrs = {
                classes("text-slate-400", "flex-shrink-0", "transition-transform")
                style { property("transform", if (expanded) "rotate(180deg)" else "rotate(0deg)") }
            }) { Text("▾") }
        }

        // Dropdown list
        if (expanded) {
            // Backdrop to close on outside click
            Div(attrs = {
                classes("fixed", "inset-0", "z-40")
                onClick { expanded = false }
            }) {}

            Div(attrs = {
                classes(
                    "absolute", "top-full", "left-0", "right-0", "mt-1", "z-50",
                    "bg-white", "border", "border-slate-200", "rounded-xl", "shadow-xl",
                    "overflow-hidden", "snackbar-enter"
                )
                style { property("max-height", "240px"); property("overflow-y", "auto") }
            }) {
                items.forEach { item ->
                    val isSelected = item == selectedItem
                    Div(attrs = {
                        classes(
                            "px-4", "py-2.5", "text-sm", "cursor-pointer", "transition-colors",
                            if (isSelected) "bg-amber-50 text-amber-700 font-semibold"
                            else "text-slate-700 hover:bg-slate-50"
                        )
                        onClick { onItemSelected(item); expanded = false }
                    }) {
                        Div(attrs = { classes("flex", "items-center", "justify-between") }) {
                            Text(itemToString(item))
                            if (isSelected) Span(attrs = { classes("text-amber-500") }) { Text("✓") }
                        }
                    }
                }
            }
        }
    }
}
