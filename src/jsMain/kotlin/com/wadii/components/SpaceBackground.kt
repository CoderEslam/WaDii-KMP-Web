package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

/**
 * Wrapper that renders content above the app's space/nebula background.
 * The actual background is defined in index.html (body + body::before CSS).
 * This composable just ensures content sits correctly in the z-index stack.
 */
@Composable
fun SpaceBackground(content: @Composable () -> Unit) {
    Div(attrs = {
        classes("relative", "min-h-screen", "w-full")
        style { property("z-index", "1") }
    }) {
        content()
    }
}
