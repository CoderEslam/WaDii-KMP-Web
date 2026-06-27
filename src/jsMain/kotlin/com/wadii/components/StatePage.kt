package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

/** Step progress indicator: numbered circles connected by lines. */
@Composable
fun StatePage(
    count: Int,
    selected: Int
) {
    Div(attrs = { classes("flex", "items-center", "w-full") }) {
        for (index in 1..count) {
            val isDone = index <= selected
            val isActive = index == selected

            // Step circle
            Div(attrs = {
                classes(
                    "w-7", "h-7", "rounded-full", "flex", "items-center", "justify-center",
                    "text-xs", "font-bold", "flex-shrink-0", "transition-all",
                    when {
                        isDone   -> "bg-amber-500 text-white"
                        isActive -> "bg-amber-100 text-amber-700 border-2 border-amber-500"
                        else     -> "bg-slate-100 text-slate-400"
                    }
                )
            }) { Text(if (isDone && !isActive) "✓" else index.toString()) }

            // Connector line
            if (index != count) {
                Div(attrs = {
                    classes(
                        "flex-1", "h-0.5", "mx-1", "transition-all",
                        if (index < selected) "bg-amber-500" else "bg-slate-200"
                    )
                }) {}
            }
        }
    }
}
