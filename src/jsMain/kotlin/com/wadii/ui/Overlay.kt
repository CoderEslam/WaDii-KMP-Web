package com.wadii.ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

enum class ModalVariant { Default, PopUp, Form }

@Composable
fun Modal(
    open: Boolean,
    title: String,
    onDismiss: () -> Unit,
    variant: ModalVariant = ModalVariant.Default,
    icon: String? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (!open) return
    Div(attrs = {
        classes("fixed", "inset-0", "z-40", "bg-black/50", "flex", "items-center", "justify-center", "p-4")
        style { property("backdrop-filter", "blur(2px)") }
        attr("role", "presentation")
        onClick { onDismiss() }
    }) {
        Div(attrs = {
            classes("bg-surface", "rounded-neu-base", "shadow-neu-xl", "w-full", "max-w-md")
            style { property("max-height", "90vh"); property("overflow-y", "auto") }
            attr("role", "dialog")
            attr("aria-modal", "true")
            onClick { it.stopPropagation() }
        }) {
            when (variant) {
                ModalVariant.PopUp -> {
                    Div(attrs = { classes("p-6", "text-center") }) {
                        if (icon != null) {
                            Div(attrs = {
                                classes("mx-auto", "mb-4", "flex", "items-center", "justify-center", "rounded-full", "bg-surface-secondary", "text-2xl")
                                style { property("width", "48px"); property("height", "48px") }
                            }) { Text(icon) }
                        }
                        H2(attrs = { classes("text-lg", "font-semibold", "text-heading", "mb-2") }) { Text(title) }
                        Div(attrs = { classes("text-sm", "text-body") }) { content() }
                        if (footer != null) {
                            Div(attrs = { classes("mt-6", "flex", "items-center", "justify-center", "gap-3") }) { footer() }
                        }
                    }
                }
                else -> {
                    Div(attrs = { classes("flex", "items-center", "justify-between", "px-5", "py-4", "border-b", "border-default") }) {
                        H2(attrs = { classes("text-xl", "font-semibold", "text-heading") }) { Text(title) }
                        ModalCloseButton(onDismiss)
                    }
                    Div(attrs = { classes("p-5", "flex", "flex-col", if (variant == ModalVariant.Form) "gap-4" else "gap-6") }) {
                        content()
                    }
                    if (footer != null) {
                        Div(attrs = { classes("px-5", "py-4", "border-t", "border-default", "flex", "items-center", "justify-end", "gap-3") }) { footer() }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalCloseButton(onClick: () -> Unit) {
    Button(attrs = {
        classes("rounded-neu-base", "text-body", "hover:shadow-neu-sm", "active:shadow-neu-inset", "transition-all", "flex", "items-center", "justify-center")
        style { property("padding", "6px"); property("background", "var(--surface)"); property("border", "none"); property("cursor", "pointer") }
        onClick { onClick() }
    }) { Text("✕") }
}

enum class AlertVariant { Brand, Success, Danger, Warning }

private fun alertTokens(variant: AlertVariant): Triple<String, String, String> = when (variant) {
    AlertVariant.Brand   -> Triple("bg-brand-softer", "border-brand-subtle", "text-fg-brand-strong")
    AlertVariant.Success -> Triple("bg-success-soft", "border-success-subtle", "text-fg-success-strong")
    AlertVariant.Danger  -> Triple("bg-danger-soft", "border-danger-subtle", "text-fg-danger-strong")
    AlertVariant.Warning -> Triple("bg-warning-soft", "border-warning-subtle", "text-fg-warning")
}

@Composable
fun Alert(variant: AlertVariant, body: String, heading: String? = null, onDismiss: (() -> Unit)? = null) {
    val (bg, border, text) = alertTokens(variant)
    Div(attrs = { classes(bg, border, text, "border", "rounded-neu-base", "p-4", "flex", "items-start", "gap-3") }) {
        Div(attrs = { classes("flex-1") }) {
            if (heading != null) P(attrs = { classes("text-base", "font-medium", "mb-1") }) { Text(heading) }
            P(attrs = { classes("text-sm"); style { property("line-height", "1.6") } }) { Text(body) }
        }
        if (onDismiss != null) {
            Button(attrs = {
                classes("flex-shrink-0", "opacity-60", "hover:opacity-100", "transition-opacity")
                style { property("background", "none"); property("border", "none"); property("cursor", "pointer"); property("color", "inherit"); property("padding", "0") }
                onClick { onDismiss() }
            }) { Text("✕") }
        }
    }
}

@Composable
fun Tooltip(text: String, dark: Boolean = true, content: @Composable () -> Unit) {
    Div(attrs = { classes("relative", "inline-flex", "group") }) {
        content()
        Span(attrs = {
            classes(*classNames(
                "absolute", "bottom-full", "left-1/2", "-translate-x-1/2", "mb-2",
                "px-3", "py-2", "text-sm", "font-medium", "rounded-neu-default", "shadow-neu-sm",
                "whitespace-nowrap", "opacity-0", "group-hover:opacity-100", "transition-opacity",
                "pointer-events-none", "z-50",
                if (dark) "bg-dark text-white" else "bg-surface-primary-medium text-heading border border-default"
            ))
        }) { Text(text) }
    }
}

@Composable
fun Popover(open: Boolean, onDismiss: () -> Unit, title: String? = null, content: @Composable () -> Unit) {
    if (!open) return
    Div(attrs = {
        classes("absolute", "z-50", "mt-2", "bg-surface", "rounded-neu-base", "shadow-neu-md", "border", "border-default")
        style { property("min-width", "220px") }
    }) {
        if (title != null) {
            Div(attrs = { classes("px-3", "py-2", "bg-surface-secondary", "border-b", "border-default") }) {
                P(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text(title) }
            }
        }
        Div(attrs = { classes("p-4") }) { content() }
    }
}

@Composable
fun <T> Dropdown(
    trigger: @Composable (expanded: Boolean, toggle: () -> Unit) -> Unit,
    items: List<T>,
    itemLabel: (T) -> String,
    onSelect: (T) -> Unit,
    itemIcon: ((T) -> String?)? = null,
    header: (@Composable () -> Unit)? = null,
    searchable: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val filtered = if (searchable && query.isNotBlank()) items.filter { itemLabel(it).contains(query, ignoreCase = true) } else items

    Div(attrs = { classes("relative", "inline-block") }) {
        trigger(expanded) { expanded = !expanded }
        if (expanded) {
            Div(attrs = {
                classes("absolute", "mt-2", "bg-surface", "border", "border-default", "rounded-neu-base", "shadow-neu-lg", "z-50")
                style { property("min-width", "176px"); property("max-height", "192px"); property("overflow-y", "auto") }
            }) {
                if (header != null) {
                    Div(attrs = { classes("px-4", "py-3", "border-b", "border-default") }) { header() }
                }
                if (searchable) {
                    Div(attrs = { classes("p-2") }) {
                        Input(type = InputType.Text, attrs = {
                            classes("w-full", "px-3", "py-2", "text-sm", "rounded-neu-default", "bg-surface", "shadow-neu-inset", "border", "border-default-medium")
                            attr("placeholder", "Search…")
                            attr("value", query)
                            onInput { query = it.value }
                        })
                    }
                }
                Div(attrs = { classes("p-2") }) {
                    filtered.forEach { item ->
                        Div(attrs = {
                            classes(
                                "flex", "items-center", "px-2", "py-2", "rounded-neu-default", "text-sm", "font-medium",
                                "text-body", "hover:bg-surface-tertiary-medium", "hover:text-heading", "transition-colors"
                            )
                            style { property("cursor", "pointer") }
                            onClick { onSelect(item); expanded = false }
                        }) {
                            val icon = itemIcon?.invoke(item)
                            if (icon != null) Span(attrs = { classes("mr-2", "text-body") }) { Text(icon) }
                            Text(itemLabel(item))
                        }
                    }
                }
            }
        }
    }
}
