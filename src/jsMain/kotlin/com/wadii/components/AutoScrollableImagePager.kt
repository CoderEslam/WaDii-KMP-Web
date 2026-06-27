package com.wadii.components

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun <T> AutoScrollableImagePager(
    images: List<T>,
    imageName: (T) -> String,
    baseUrl: String = "",
    intervalMillis: Long = 3000L,
    showIndicator: Boolean = true,
    onItemClick: (T) -> Unit = {}
) {
    if (images.isEmpty()) return

    var currentPage by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalMillis)
            currentPage = (currentPage + 1) % images.size
        }
    }

    Div(attrs = {
        classes("relative", "w-full", "overflow-hidden", "rounded-2xl")
        style { property("aspect-ratio", "16/9") }
    }) {
        images.forEachIndexed { index, item ->
            Div(attrs = {
                classes("absolute", "inset-0", "transition-opacity", "duration-700", "cursor-pointer")
                style { property("opacity", if (index == currentPage) "1" else "0") }
                onClick { onItemClick(item) }
            }) {
                Img(
                    src = "$baseUrl${imageName(item)}",
                    attrs = { classes("w-full", "h-full", "object-cover") }
                )
            }
        }

        if (showIndicator && images.size > 1) {
            Div(attrs = {
                classes("absolute", "bottom-3", "left-0", "right-0", "flex", "justify-center", "gap-2")
            }) {
                images.forEachIndexed { index, _ ->
                    Div(attrs = {
                        val active = index == currentPage
                        classes(
                            "rounded-full", "transition-all", "duration-300",
                            if (active) "w-5 h-2 bg-amber-500" else "w-2 h-2 bg-white/60"
                        )
                        onClick { currentPage = index }
                        style { property("cursor", "pointer") }
                    }) {}
                }
            }
        }

        // Prev / Next tap areas
        if (images.size > 1) {
            Div(attrs = {
                classes("absolute", "left-0", "top-0", "h-full", "w-1/4", "cursor-pointer")
                onClick { currentPage = if (currentPage == 0) images.lastIndex else currentPage - 1 }
            }) {}
            Div(attrs = {
                classes("absolute", "right-0", "top-0", "h-full", "w-1/4", "cursor-pointer")
                onClick { currentPage = (currentPage + 1) % images.size }
            }) {}
        }
    }
}
