package com.wadii.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

/** One to five tappable stars. [stars] = 0 means nothing is picked yet. */
@Composable
fun StarPicker(stars: Int, error: String? = null, onPick: (Int) -> Unit) {
    Div(attrs = { classes("flex", "gap-1", "mb-1"); attr("role", "radiogroup"); attr("aria-label", "Star rating") }) {
        (1..5).forEach { n ->
            Button(attrs = {
                classes("text-3xl", "leading-none", "p-1", "transition-transform", "hover:scale-110")
                attr("type", "button")
                attr("aria-label", "$n star${if (n > 1) "s" else ""}")
                style {
                    property("background", "none"); property("border", "none"); property("cursor", "pointer")
                    property("opacity", if (n <= stars) "1" else "0.3")
                }
                onClick { onPick(n) }
            }) { Text("⭐") }
        }
    }
    error?.let { P(attrs = { classes("field-error", "mb-2"); attr("role", "alert") }) { Text(it) } }
}
