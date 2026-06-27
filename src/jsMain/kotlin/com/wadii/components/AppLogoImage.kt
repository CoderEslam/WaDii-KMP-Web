package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun AppLogoImage(size: String = "48px") {
    Div(attrs = {
        classes("flex", "items-center", "justify-center")
        style { property("width", size); property("height", size) }
    }) {
        Span(attrs = {
            classes("font-extrabold", "brand-text", "tracking-tight")
            style { property("font-size", "calc($size * 0.5)") }
        }) { Text("W") }
    }
}
