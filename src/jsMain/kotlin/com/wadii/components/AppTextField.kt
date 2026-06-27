package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    type: String = "text",
    isError: Boolean = false,
    errorMessage: String = "",
    leadingIcon: String? = null,
    trailingIcon: String? = null,
    readOnly: Boolean = false
) {
    Div(attrs = { classes("flex", "flex-col", "gap-1.5") }) {
        if (label.isNotEmpty()) {
            Label(attrs = { classes("text-xs", "font-semibold", "text-slate-600", "uppercase", "tracking-wide") }) {
                Text(label)
            }
        }
        Div(attrs = {
            classes(
                "flex", "items-center", "gap-2",
                "w-full", "px-4", "py-2.5",
                "border", "rounded-xl", "bg-white", "text-sm",
                if (isError) "border-red-400" else "border-slate-300",
                "focus-within:ring-2",
                if (isError) "focus-within:ring-red-300" else "focus-within:ring-amber-400",
                "focus-within:border-transparent", "transition-all"
            )
        }) {
            leadingIcon?.let {
                Span(attrs = { classes("text-slate-400", "flex-shrink-0") }) { Text(it) }
            }
            Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
                classes("flex-1", "outline-none", "bg-transparent", "text-slate-800", "placeholder-slate-400")
                attr("type", type)
                attr("placeholder", placeholder)
                attr("value", value)
                if (readOnly) attr("readonly", "true")
                onInput { onValueChange(it.value) }
            })
            trailingIcon?.let {
                Span(attrs = { classes("text-slate-400", "flex-shrink-0") }) { Text(it) }
            }
        }
        if (isError && errorMessage.isNotEmpty()) {
            Span(attrs = { classes("text-xs", "text-red-500", "mt-0.5") }) { Text(errorMessage) }
        }
    }
}
