package com.wadii.components

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

data class LocationInfo(
    val description: String = "",
    val mapLink: String = ""
)

@Composable
fun LocationDialog(
    info: LocationInfo,
    onDismiss: () -> Unit
) {
    // Backdrop
    Div(attrs = {
        classes("fixed", "inset-0", "z-50", "flex", "items-center", "justify-center", "bg-black/50")
        style { property("backdrop-filter", "blur(4px)") }
        onClick { onDismiss() }
    }) {
        // Card — stop click propagation so clicking inside doesn't close
        Div(attrs = {
            classes("bg-white", "rounded-2xl", "p-6", "w-full", "shadow-2xl", "flex", "flex-col", "gap-4")
            style { property("max-width", "420px"); property("margin", "16px") }
            onClick { it.stopPropagation() }
        }) {
            // Header
            Div(attrs = { classes("flex", "items-center", "gap-2") }) {
                Span(attrs = { classes("text-xl") }) { Text("📍") }
                P(attrs = { classes("text-base", "font-bold", "text-slate-800") }) { Text("Location") }
            }

            // Body
            if (info.description.isNotEmpty()) {
                P(attrs = { classes("text-sm", "text-slate-600", "leading-relaxed") }) { Text(info.description) }
            }
            if (info.description.isEmpty() && info.mapLink.isEmpty()) {
                P(attrs = { classes("text-sm", "text-slate-400") }) { Text("No location information available.") }
            }

            // Actions
            Div(attrs = { classes("flex", "items-center", "justify-end", "gap-2", "pt-1") }) {
                Button(attrs = {
                    classes("px-4", "py-2", "text-sm", "text-slate-500", "hover:text-slate-700",
                        "rounded-xl", "hover:bg-slate-100", "transition-colors",
                        "border-none", "bg-transparent", "cursor-pointer")
                    onClick { onDismiss() }
                }) { Text("Close") }

                if (info.mapLink.isNotEmpty()) {
                    val label = when {
                        info.mapLink.isGoogleMapsUrl() -> "Google Maps"
                        info.mapLink.isAppleMapsUrl()  -> "Apple Maps"
                        else                           -> "Open Link"
                    }
                    Button(attrs = {
                        classes("px-4", "py-2", "text-sm", "font-semibold", "text-amber-600",
                            "hover:bg-amber-50", "rounded-xl", "transition-colors",
                            "border-none", "bg-transparent", "cursor-pointer")
                        onClick { window.open(info.mapLink, "_blank"); onDismiss() }
                    }) { Text("↗ $label") }
                }
            }
        }
    }
}

private fun String.isGoogleMapsUrl() =
    contains("maps.google.com") || contains("google.com/maps") || contains("goo.gl/maps")

private fun String.isAppleMapsUrl() = contains("maps.apple.com")
