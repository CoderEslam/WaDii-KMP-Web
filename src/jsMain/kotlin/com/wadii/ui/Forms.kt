package com.wadii.ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun Checkbox(id: String, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, disabled: Boolean = false) {
    Label(attrs = {
        attr("for", id)
        classes("inline-flex", "items-center", "gap-2", if (disabled) "cursor-not-allowed" else "cursor-pointer")
    }) {
        CheckboxInput(checked = checked, attrs = {
            attr("id", id)
            classes("rounded-neu-sm")
            style { property("width", "16px"); property("height", "16px"); property("accent-color", "var(--brand)") }
            onInput { onCheckedChange(it.value) }
            if (disabled) disabled()
        })
        Span(attrs = { classes("text-sm", if (disabled) "text-fg-disabled" else "text-body") }) { Text(label) }
    }
}

@Composable
fun <T> RadioGroup(name: String, options: List<T>, selected: T, itemLabel: (T) -> String, onSelect: (T) -> Unit) {
    Div(attrs = { classes("flex", "flex-col", "gap-3") }) {
        options.forEachIndexed { i, option ->
            val id = "$name-$i"
            val isChecked = option == selected
            Label(attrs = { attr("for", id); classes("inline-flex", "items-center", "gap-2", "cursor-pointer") }) {
                RadioInput(checked = isChecked, attrs = {
                    attr("id", id)
                    attr("name", name)
                    style { property("width", "16px"); property("height", "16px"); property("accent-color", "var(--brand)") }
                    onInput { if (it.value) onSelect(option) }
                })
                Span(attrs = { classes("text-sm", "text-body") }) { Text(itemLabel(option)) }
            }
        }
    }
}

@Composable
fun Toggle(id: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, disabled: Boolean = false, label: String? = null) {
    Label(attrs = {
        attr("for", id)
        classes("inline-flex", "items-center", "gap-3", if (disabled) "cursor-not-allowed" else "cursor-pointer")
    }) {
        Div(attrs = {
            classes("relative", "rounded-full", "transition-colors", "flex-shrink-0")
            style {
                property("width", "40px"); property("height", "22px")
                property("background", if (disabled) "var(--surface-tertiary)" else if (checked) "var(--brand)" else "var(--surface-quaternary)")
            }
        }) {
            CheckboxInput(checked = checked, attrs = {
                attr("id", id)
                style {
                    property("position", "absolute"); property("inset", "0"); property("opacity", "0")
                    property("margin", "0"); property("cursor", if (disabled) "not-allowed" else "pointer")
                }
                onInput { onCheckedChange(it.value) }
                if (disabled) disabled()
            })
            Div(attrs = {
                classes("absolute", "rounded-full", "bg-toggle-thumb", "transition-transform")
                style {
                    property("top", "2px"); property("left", "2px")
                    property("width", "18px"); property("height", "18px")
                    property("border", "2px solid var(--border-buffer)")
                    property("transform", if (checked) "translateX(18px)" else "translateX(0)")
                    property("pointer-events", "none")
                }
            }) {}
        }
        if (label != null) Span(attrs = { classes("text-sm", if (disabled) "text-fg-disabled" else "text-body") }) { Text(label) }
    }
}

@Composable
fun ButtonGroup(content: @Composable () -> Unit) {
    Div(attrs = { classes("neu-button-group") }) { content() }
}

@Composable
fun <T> SelectField(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit
) {
    Div(attrs = { classes("flex", "flex-col", "gap-2") }) {
        Label(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text(label) }
        Select(attrs = {
            classes(
                "w-full", "px-4", "py-2.5", "border", "border-default-medium", "rounded-neu-base",
                "bg-surface", "shadow-neu-inset", "text-sm", "text-heading",
                "focus:outline-none", "focus:ring-1", "focus:ring-brand", "focus:border-brand"
            )
            onChange { event ->
                val idx = (event.target as? org.w3c.dom.HTMLSelectElement)?.selectedIndex ?: -1
                if (idx in options.indices) onSelect(options[idx])
            }
        }) {
            options.forEach { opt ->
                Option(value = optionLabel(opt), attrs = { if (opt == selected) attr("selected", "true") }) {
                    Text(optionLabel(opt))
                }
            }
        }
    }
}
