package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun AppTextFieldOutlined(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    type: String = "text",
    isError: Boolean = false,
    errorMessage: String = "",
    leadingIcon: String? = null,
    passwordVisible: Boolean = true,
    readOnly: Boolean = false
) {
    Div(attrs = { classes("flex", "flex-col", "gap-1") }) {
        Div(attrs = { classes("relative") }) {
            if (label.isNotEmpty()) {
                Label(attrs = {
                    classes(
                        "absolute", "-top-2.5", "left-3", "px-1", "text-xs", "font-semibold", "bg-white",
                        if (isError) "text-red-500" else "text-amber-600"
                    )
                }) { Text(label) }
            }
            Div(attrs = {
                classes(
                    "flex", "items-center", "gap-2", "w-full", "rounded-xl", "border",
                    "bg-white", "transition-all",
                    if (isError) "border-red-400" else "border-slate-300",
                    "focus-within:ring-2",
                    if (isError) "focus-within:ring-red-300" else "focus-within:ring-amber-400",
                    "focus-within:border-transparent"
                )
            }) {
                leadingIcon?.let {
                    Span(attrs = { classes("pl-3", "text-slate-400", "flex-shrink-0") }) { Text(it) }
                }
                Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
                    classes("flex-1", "px-4", "py-3", "outline-none", "bg-transparent", "text-sm", "text-slate-800", "placeholder-slate-400", "rounded-xl")
                    attr("type", if (!passwordVisible) "password" else type)
                    attr("placeholder", placeholder)
                    attr("value", value)
                    if (readOnly) attr("readonly", "true")
                    onInput { onValueChange(it.value) }
                })
            }
        }
        if (isError && errorMessage.isNotEmpty()) {
            Span(attrs = { classes("text-xs", "text-red-500", "ml-1") }) { Text("⚠ $errorMessage") }
        }
    }
}
