package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

/** Full-screen translucent loading overlay with a centered amber spinner. */
@Composable
fun LoadingOverlay() {
    Div(attrs = {
        classes("fixed", "inset-0", "z-50", "flex", "items-center", "justify-center")
        style { property("background", "rgba(255,255,255,0.65)"); property("backdrop-filter", "blur(4px)") }
    }) {
        Div(attrs = {
            classes("bg-white", "rounded-2xl", "p-6", "flex", "flex-col", "items-center", "gap-3", "shadow-xl")
        }) {
            Div(attrs = { classes("relative", "w-11", "h-11") }) {
                Div(attrs = { classes("w-11", "h-11", "border-4", "border-amber-500", "border-t-transparent", "rounded-full", "animate-spin") }) {}
                Div(attrs = {
                    style {
                        property("position", "absolute")
                        property("inset", "4px")
                        property("border", "2px solid transparent")
                        property("border-bottom-color", "rgba(139,92,246,0.4)")
                        property("border-radius", "50%")
                        property("animation", "spin 1.5s linear infinite reverse")
                    }
                }) {}
            }
            P(attrs = { classes("text-xs", "text-slate-400", "tracking-widest", "uppercase") }) { Text("Loading…") }
        }
    }
}
