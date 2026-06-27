package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

enum class SelectionOrientation { HORIZONTAL, VERTICAL }

@Composable
fun <T> RadioGroup(
    options: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    orientation: SelectionOrientation = SelectionOrientation.VERTICAL,
    itemToString: (T) -> String = { it.toString() },
    itemToIcon: (T) -> String? = { null }
) {
    val layoutClass = if (orientation == SelectionOrientation.HORIZONTAL)
        "flex flex-wrap gap-3" else "flex flex-col gap-2"

    Div(attrs = { attr("class", layoutClass) }) {
        options.forEach { option ->
            val isSelected = option == selectedItem
            Div(attrs = {
                classes(
                    "flex", "items-center", "gap-3", "px-4", "py-3", "rounded-xl",
                    "cursor-pointer", "border", "transition-all",
                    if (isSelected) "border-amber-400 bg-amber-50"
                    else "border-slate-200 bg-white hover:border-amber-300"
                )
                onClick { onItemSelected(option) }
            }) {
                // Radio circle
                Div(attrs = {
                    classes(
                        "w-4", "h-4", "rounded-full", "border-2", "flex", "items-center",
                        "justify-center", "flex-shrink-0", "transition-all",
                        if (isSelected) "border-amber-500" else "border-slate-300"
                    )
                }) {
                    if (isSelected) {
                        Div(attrs = { classes("w-2", "h-2", "rounded-full", "bg-amber-500") }) {}
                    }
                }
                itemToIcon(option)?.let {
                    Span(attrs = { classes("text-base") }) { Text(it) }
                }
                Span(attrs = {
                    classes("text-sm", if (isSelected) "font-semibold text-slate-800" else "text-slate-600")
                }) { Text(itemToString(option)) }
            }
        }
    }
}
