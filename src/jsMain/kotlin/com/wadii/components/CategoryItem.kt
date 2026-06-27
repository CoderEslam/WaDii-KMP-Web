package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun <T> CategoryItem(
    list: List<T>,
    selected: T,
    itemToString: (T) -> String = { it.toString() },
    onClick: (T) -> Unit = {}
) {
    Div(attrs = {
        classes("flex", "flex-wrap", "gap-2", "p-1")
        style { property("overflow-x", "auto") }
    }) {
        list.forEach { item ->
            val isSelected = item == selected
            Button(attrs = {
                classes(
                    "px-5", "py-2", "rounded-full", "text-sm", "font-medium",
                    "transition-all", "border", "cursor-pointer", "whitespace-nowrap",
                    if (isSelected) "bg-amber-500 text-white border-amber-500 font-semibold"
                    else "bg-white text-slate-600 border-slate-200 hover:border-amber-400 hover:text-amber-600"
                )
                onClick { onClick(item) }
            }) {
                Text(itemToString(item))
            }
        }
    }
}
