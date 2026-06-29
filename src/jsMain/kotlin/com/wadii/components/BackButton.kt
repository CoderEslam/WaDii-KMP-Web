package com.wadii.components

import androidx.compose.runtime.*
import com.wadii.images.BACK_ARROW
import com.wadii.images.PARKING_CAR
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLButtonElement

@Composable
fun BackButton(
    text: String = "",
    showBackArrow: Boolean = true,
    onClick: () -> Unit = {}
) {
    Button(attrs = {
        classes(
            "inline-flex", "items-center", "gap-2", "px-3", "py-2",
            "text-sm", "font-medium", "text-amber-600",
            "hover:bg-amber-50", "rounded-xl", "transition-colors",
            "border-none", "bg-transparent", "cursor-pointer"
        )
        onClick { onClick() }
    }) {
        if (showBackArrow) {
//            Img(
//                src = BACK_ARROW,
//                attrs = {
//                    style {
//                        property("height", "20px");
//                        property("width", "20px")
//                    }
//                    classes(
//                        "inline-flex",
//                        "items-center",
//                        "justify-center",
//                        "rotate-90",
//                        "bg-blue-500"
//                    )
//                }
//            )
            Span(attrs = {
                classes("inline-flex", "items-center", "justify-center")
                style { property("width", "20px");
                    property("height", "20px") }
                ref { element ->
                    element.innerHTML =
                        """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" width="20" height="20"><polyline points="15 18 9 12 15 6"/></svg>"""
                    onDispose {}
                }
            })
        }
        if (text.isNotEmpty()) {
            Span { Text(text) }
        }
    }
}
