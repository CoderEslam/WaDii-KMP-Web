package com.wadii.ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

enum class TabVariant { Underline, Pills, FullWidth }

@Composable
fun Tabs(
    tabs: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    variant: TabVariant = TabVariant.Underline,
    icons: List<String>? = null
) {
    when (variant) {
        TabVariant.Underline -> Div(attrs = { classes("flex", "border-b", "border-default") }) {
            tabs.forEachIndexed { i, label ->
                val active = i == selected
                Button(attrs = {
                    classes(*classNames(
                        "px-4", "py-4", "text-sm", "font-medium", "transition-colors",
                        "rounded-t-neu-base", "bg-transparent", "flex", "items-center", "gap-2",
                        if (active) "text-fg-brand" else "text-body hover:text-heading"
                    ))
                    style {
                        property("border", "none")
                        property("border-bottom", "2px solid " + if (active) "var(--border-brand)" else "transparent")
                        property("cursor", "pointer")
                    }
                    onClick { onSelect(i) }
                }) {
                    icons?.getOrNull(i)?.let { Span { Text(it) } }
                    Text(label)
                }
            }
        }
        TabVariant.Pills -> Div(attrs = { classes("flex", "gap-2") }) {
            tabs.forEachIndexed { i, label ->
                val active = i == selected
                Button(attrs = {
                    classes(*classNames(
                        "px-4", "py-2.5", "rounded-neu-base", "text-sm", "font-medium", "transition-all",
                        "flex", "items-center", "gap-2",
                        if (active) "bg-brand text-white shadow-neu-sm" else "bg-transparent text-body hover:bg-surface-secondary hover:text-heading"
                    ))
                    style { property("border", "none"); property("cursor", "pointer") }
                    onClick { onSelect(i) }
                }) {
                    icons?.getOrNull(i)?.let { Span { Text(it) } }
                    Text(label)
                }
            }
        }
        TabVariant.FullWidth -> Div(attrs = { classes("flex") }) {
            tabs.forEachIndexed { i, label ->
                val active = i == selected
                Button(attrs = {
                    classes(*classNames(
                        "flex-1", "px-4", "py-4", "text-sm", "font-medium", "text-center", "transition-colors",
                        "bg-surface", "border", "border-default",
                        if (i > 0) "-ml-px" else null,
                        if (i == 0) "rounded-l-neu-base" else null,
                        if (i == tabs.lastIndex) "rounded-r-neu-base" else null,
                        if (active) "bg-surface-secondary text-fg-brand" else "text-body hover:bg-surface-secondary-medium hover:text-heading"
                    ))
                    style { property("cursor", "pointer") }
                    onClick { onSelect(i) }
                }) { Text(label) }
            }
        }
    }
}

@Composable
fun Pagination(page: Int, totalPages: Int, onPageChange: (Int) -> Unit) {
    if (totalPages <= 1) return
    Div(attrs = { classes("flex", "text-sm") }) {
        Button(attrs = {
            classes(
                "flex", "items-center", "justify-center", "bg-surface-secondary-medium",
                "border", "border-default-medium", "rounded-l-neu-base", "text-body", "font-medium",
                "hover:bg-surface-tertiary-medium", "hover:text-heading", "transition-colors",
                "disabled:opacity-50", "disabled:cursor-not-allowed"
            )
            style { property("height", "36px"); property("padding", "0 12px"); property("cursor", "pointer") }
            onClick { if (page > 1) onPageChange(page - 1) }
            if (page <= 1) disabled()
        }) { Text("‹") }
        (1..totalPages).forEach { p ->
            val active = p == page
            Button(attrs = {
                classes(*classNames(
                    "flex", "items-center", "justify-center", "-ml-px",
                    "border", "border-default-medium", "font-medium", "transition-colors",
                    if (active) "bg-surface-tertiary-medium text-fg-brand" else "bg-surface-secondary-medium text-body hover:bg-surface-tertiary-medium hover:text-heading"
                ))
                style { property("height", "36px"); property("width", "36px"); property("cursor", "pointer"); property("outline", "none") }
                onClick { onPageChange(p) }
            }) { Text(p.toString()) }
        }
        Button(attrs = {
            classes(
                "flex", "items-center", "justify-center", "-ml-px", "bg-surface-secondary-medium",
                "border", "border-default-medium", "rounded-r-neu-base", "text-body", "font-medium",
                "hover:bg-surface-tertiary-medium", "hover:text-heading", "transition-colors",
                "disabled:opacity-50", "disabled:cursor-not-allowed"
            )
            style { property("height", "36px"); property("padding", "0 12px"); property("cursor", "pointer") }
            onClick { if (page < totalPages) onPageChange(page + 1) }
            if (page >= totalPages) disabled()
        }) { Text("›") }
    }
}

data class AccordionItem(val title: String, val content: @Composable () -> Unit)
enum class AccordionVariant { Default, Separated, AlwaysOpen, Flush }

@Composable
fun Accordion(items: List<AccordionItem>, variant: AccordionVariant = AccordionVariant.Default, allowMultiple: Boolean = false) {
    val openState = remember { mutableStateMapOf<Int, Boolean>() }
    val effectiveMultiple = allowMultiple || variant == AccordionVariant.AlwaysOpen

    fun toggle(i: Int) {
        val isOpen = openState[i] ?: false
        if (!effectiveMultiple) openState.clear()
        openState[i] = !isOpen
    }

    val wrapped = variant == AccordionVariant.Default || variant == AccordionVariant.AlwaysOpen

    Div(attrs = {
        classes(*classNames(if (wrapped) "border border-default rounded-neu-base overflow-hidden" else "flex flex-col"))
    }) {
        items.forEachIndexed { i, item ->
            val open = openState[i] ?: false
            when (variant) {
                AccordionVariant.Separated -> Div(attrs = { classes("border", "border-default", "rounded-neu-base", "shadow-neu-sm", "mb-2", "overflow-hidden") }) {
                    AccordionTriggerAndPanel(item, open, i, ::toggle, flush = false)
                }
                AccordionVariant.Flush -> Div(attrs = { attr("class", if (i < items.lastIndex) "border-b border-default" else "") }) {
                    AccordionTriggerAndPanel(item, open, i, ::toggle, flush = true)
                }
                else -> Div(attrs = { attr("class", if (i < items.lastIndex) "border-b border-default" else "") }) {
                    AccordionTriggerAndPanel(item, open, i, ::toggle, flush = false)
                }
            }
        }
    }
}

@Composable
private fun AccordionTriggerAndPanel(item: AccordionItem, open: Boolean, index: Int, onToggle: (Int) -> Unit, flush: Boolean) {
    Button(attrs = {
        classes(*classNames(
            "w-full", "flex", "items-center", "justify-between", "px-5", "py-4", "text-sm", "font-medium",
            "text-heading", "transition-colors",
            if (flush) "bg-transparent hover:text-fg-brand" else (if (open) "bg-surface-tertiary" else "bg-surface-secondary") + " hover:bg-surface-tertiary"
        ))
        style { property("border", "none"); property("cursor", "pointer"); property("outline", "none") }
        onClick { onToggle(index) }
    }) {
        Text(item.title)
        Span(attrs = {
            classes("text-body")
            style {
                property("display", "inline-block")
                property("transition", "transform 150ms")
                property("transform", if (open) "rotate(180deg)" else "rotate(0deg)")
            }
        }) { Text("⌄") }
    }
    if (open) {
        Div(attrs = {
            classes("px-5", "py-4", "bg-surface", "border-t", "border-default", "text-sm", "text-body")
            style { property("line-height", "1.625") }
        }) { item.content() }
    }
}
