package com.wadii.ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Table as HtmlTable
import org.jetbrains.compose.web.dom.Tbody
import org.jetbrains.compose.web.dom.Td
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Th
import org.jetbrains.compose.web.dom.Thead
import org.jetbrains.compose.web.dom.Tr

@Composable
fun Table(headers: List<String>, rows: List<List<@Composable () -> Unit>>, rowHeaderIndex: Int = 0) {
    Div(attrs = {
        classes("bg-surface", "rounded-neu-base", "border", "border-default", "shadow-neu-sm")
        style { property("overflow-x", "auto") }
    }) {
        HtmlTable(attrs = { classes("w-full", "text-sm", "text-body"); style { property("border-collapse", "collapse") } }) {
            Thead {
                Tr {
                    headers.forEach { h ->
                        Th(attrs = {
                            classes("text-left", "font-medium", "text-body", "bg-surface-secondary")
                            style { property("padding", "12px 24px"); property("border-bottom", "1px solid var(--border-default)") }
                        }) { Text(h) }
                    }
                }
            }
            Tbody {
                rows.forEachIndexed { ri, row ->
                    Tr {
                        row.forEachIndexed { ci, cellContent ->
                            val isLast = ri == rows.lastIndex
                            val isHeaderCell = ci == rowHeaderIndex
                            Td(attrs = {
                                attr("class", if (isHeaderCell) "font-medium text-heading" else "")
                                if (isHeaderCell) attr("scope", "row")
                                style {
                                    property("padding", "16px 24px")
                                    property("white-space", if (isHeaderCell) "nowrap" else "normal")
                                    if (!isLast) property("border-bottom", "1px solid var(--border-default)")
                                }
                            }) { cellContent() }
                        }
                    }
                }
            }
        }
    }
}

enum class AvatarSize(val px: Int, val radiusClass: String) {
    XS(18, "rounded-neu-sm"), SM(24, "rounded-neu-sm"), Base(32, "rounded-neu-base"),
    LG(44, "rounded-neu-base"), XL(56, "rounded-neu-base"), XXL(64, "rounded-neu-base")
}
enum class AvatarShape { Circle, RoundedSquare }

@Composable
fun Avatar(
    imageUrl: String? = null,
    initials: String = "",
    size: AvatarSize = AvatarSize.Base,
    shape: AvatarShape = AvatarShape.Circle,
    bordered: Boolean = false
) {
    Div(attrs = {
        classes(*classNames(
            "inline-flex", "items-center", "justify-center", "bg-surface-secondary", "text-body", "font-medium",
            "flex-shrink-0", if (shape == AvatarShape.Circle) "rounded-full" else size.radiusClass,
            if (bordered) "border-2 border-default" else null
        ))
        style {
            property("width", "${size.px}px"); property("height", "${size.px}px")
            property("font-size", "${(size.px * 0.4).toInt()}px")
            property("overflow", "hidden")
        }
    }) {
        if (imageUrl != null) {
            Img(src = imageUrl, attrs = { style { property("width", "100%"); property("height", "100%"); property("object-fit", "cover") } })
        } else {
            Text(initials)
        }
    }
}

@Composable
fun AvatarStack(count: Int, max: Int = 4, avatarAt: @Composable (Int) -> Unit) {
    Div(attrs = { classes("flex", "items-center") }) {
        val shown = minOf(count, max)
        repeat(shown) { i ->
            Div(attrs = {
                classes("rounded-full", "flex-shrink-0")
                style {
                    property("border", "2px solid var(--border-buffer)")
                    if (i > 0) property("margin-left", "-16px")
                }
            }) { avatarAt(i) }
        }
        if (count > max) {
            Div(attrs = {
                classes("rounded-full", "bg-dark-strong", "text-white", "text-xs", "font-medium", "flex", "items-center", "justify-center", "flex-shrink-0")
                style {
                    property("width", "40px"); property("height", "40px")
                    property("margin-left", "-16px"); property("border", "2px solid var(--border-buffer)")
                }
            }) { Text("+${count - max}") }
        }
    }
}

enum class IconShapeSize(val container: Int, val icon: Int, val squareRadiusClass: String) {
    XS(24, 14, "rounded-neu-default"), SM(32, 16, "rounded-neu-default"),
    MD(40, 20, "rounded-neu-base"), LG(48, 24, "rounded-neu-base"), XL(56, 28, "rounded-neu-base")
}
enum class IconShapeVariant { Brand, Gray, Danger, Success, Warning }
enum class IconShapeShape { Circle, RoundedSquare }

private fun iconShapeTokens(variant: IconShapeVariant): Pair<String, String> = when (variant) {
    IconShapeVariant.Brand   -> "bg-brand-softer" to "text-fg-brand-strong"
    IconShapeVariant.Gray    -> "bg-surface-secondary" to "text-body"
    IconShapeVariant.Danger  -> "bg-danger-soft" to "text-fg-danger-strong"
    IconShapeVariant.Success -> "bg-success-soft" to "text-fg-success-strong"
    IconShapeVariant.Warning -> "bg-warning-soft" to "text-fg-warning"
}

@Composable
fun IconShape(
    icon: String,
    size: IconShapeSize = IconShapeSize.MD,
    variant: IconShapeVariant = IconShapeVariant.Brand,
    shape: IconShapeShape = IconShapeShape.Circle
) {
    val (bg, fg) = iconShapeTokens(variant)
    Div(attrs = {
        classes("inline-flex", "items-center", "justify-center", "flex-shrink-0", bg, fg, if (shape == IconShapeShape.Circle) "rounded-full" else size.squareRadiusClass)
        style {
            property("box-sizing", "border-box")
            property("width", "${size.container}px"); property("height", "${size.container}px")
            property("font-size", "${size.icon}px")
        }
    }) { Text(icon) }
}
