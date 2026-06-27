package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

/** Glass-morphism card container matching the app's existing .bg-white glassmorphism CSS. */
@Composable
fun GlassCard(
    extraClasses: String = "",
    content: @Composable () -> Unit
) {
    Div(attrs = {
        classes("bg-white", "rounded-2xl", "p-5")
        if (extraClasses.isNotEmpty()) attr("class", "bg-white rounded-2xl p-5 $extraClasses")
    }) {
        content()
    }
}

/** Inline glass pill badge — e.g. for status tags or counts. */
@Composable
fun GlassPill(text: String, color: String = "amber") {
    val (bg, fg) = when (color) {
        "green"  -> "bg-green-50"  to "text-green-700"
        "red"    -> "bg-red-50"    to "text-red-700"
        "blue"   -> "bg-blue-50"   to "text-blue-700"
        "slate"  -> "bg-slate-100" to "text-slate-600"
        else     -> "bg-amber-50"  to "text-amber-700"
    }
    Span(attrs = { classes(bg, fg, "text-xs", "font-medium", "px-3", "py-1", "rounded-full") }) {
        Text(text)
    }
}
