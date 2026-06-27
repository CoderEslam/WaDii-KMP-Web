package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

/** Segmented tab / toggle control — one option active at a time. */
@Composable
fun <T> ToggledButtons(
    list: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    itemToString: (T) -> String = { it.toString() },
    fullWidth: Boolean = false
) {
    Div(attrs = {
        classes(
            "inline-flex", "bg-slate-100", "rounded-xl", "p-1", "gap-1",
            if (fullWidth) "w-full" else ""
        )
    }) {
        list.forEach { item ->
            val isSelected = item == selected
            Button(attrs = {
                classes(
                    "px-4", "py-2", "rounded-lg", "text-sm", "font-medium",
                    "transition-all", "border-none", "cursor-pointer",
                    if (fullWidth) "flex-1" else "",
                    if (isSelected) "bg-white text-slate-800 shadow-sm font-semibold"
                    else "text-slate-500 hover:text-slate-700 bg-transparent"
                )
                onClick { onSelected(item) }
            }) {
                Text(itemToString(item))
            }
        }
    }
}
