package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun AppTextFieldTitled(
    value: String,
    onValueChange: (String) -> Unit,
    title: String = "",
    placeholder: String = "",
    label: String = "",
    type: String = "text",
    isError: Boolean = false,
    errorMessage: String = "",
    passwordVisible: Boolean = true,
    hint: String = "",
    readOnly: Boolean = false
) {
    Div(attrs = { classes("flex", "flex-col", "gap-2") }) {
        if (title.isNotEmpty()) {
            Div(attrs = { classes("flex", "items-center", "justify-between") }) {
                P(attrs = { classes("text-sm", "font-semibold", "text-slate-700") }) { Text(title) }
                if (hint.isNotEmpty()) {
                    Span(attrs = { classes("text-xs", "text-slate-400") }) { Text(hint) }
                }
            }
        }
        if (label.isNotEmpty()) {
            Label(attrs = { classes("text-xs", "font-medium", "text-slate-500", "uppercase", "tracking-wide") }) {
                Text(label)
            }
        }
        Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
            classes(
                "w-full", "px-4", "py-2.5", "rounded-xl", "border", "text-sm",
                "bg-white", "text-slate-800", "outline-none", "placeholder-slate-400",
                "focus:ring-2", "focus:border-transparent", "transition-all",
                if (isError) "border-red-400 focus:ring-red-300" else "border-slate-300 focus:ring-amber-400"
            )
            attr("type", if (!passwordVisible) "password" else type)
            attr("placeholder", placeholder)
            attr("value", value)
            if (readOnly) attr("readonly", "true")
            onInput { onValueChange(it.value) }
        })
        if (isError && errorMessage.isNotEmpty()) {
            Span(attrs = { classes("text-xs", "text-red-500") }) { Text("⚠ $errorMessage") }
        }
    }
}
