package com.wadii.ui

import androidx.compose.runtime.*
import com.wadii.state.AppState
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun Spinner() {
    Span(attrs = { classes("spinner") }) {}
}

@Composable
fun LoadingSkeletons(count: Int = 4, height: String = "h-24") {
    Div(attrs = { classes("space-y-3") }) {
        repeat(count) {
            Div(attrs = { classes("bg-white", "rounded-xl", "animate-pulse", height) }) {}
        }
    }
}

@Composable
fun EmptyState(icon: String, message: String) {
    Div(attrs = { classes("bg-white", "rounded-2xl", "p-12", "text-center") }) {
        P(attrs = { classes("text-5xl", "mb-3") }) { Text(icon) }
        P(attrs = { classes("text-slate-500") }) { Text(message) }
    }
}

@Composable
fun Toast() {
    val msg = AppState.toastMessage ?: return
    val isError = AppState.toastIsError

    LaunchedEffect(msg) {
        delay(3000)
        AppState.clearToast()
    }

    Div(attrs = {
        classes(
            "fixed", "top-4", "right-4", "z-50",
            "px-5", "py-3", "rounded-xl", "shadow-lg",
            "text-white", "text-sm", "font-medium",
            "toast-enter",
            if (isError) "bg-red-500" else "bg-green-500"
        )
    }) {
        Text(msg)
    }
}

@Composable
fun Card(classes: String = "", content: @Composable () -> Unit) {
    Div(attrs = {
        classes("bg-white", "border", "border-slate-200", "rounded-2xl", "shadow-sm")
        if (classes.isNotEmpty()) attr("class",
            "bg-white border border-slate-200 rounded-2xl shadow-sm $classes")
    }) { content() }
}

@Composable
fun Badge(text: String, color: String = "amber") {
    val colors = when (color) {
        "green" -> "bg-green-50 text-green-700"
        "red" -> "bg-red-50 text-red-700"
        "yellow" -> "bg-yellow-50 text-yellow-700"
        "blue" -> "bg-blue-50 text-blue-700"
        else -> "bg-amber-50 text-amber-700"
    }
    Span(attrs = { classes(*colors.split(" ").toTypedArray(), "text-xs", "font-medium", "px-2", "py-0.5", "rounded-full") }) {
        Text(text)
    }
}

@Composable
fun PrimaryButton(text: String, loading: Boolean = false, fullWidth: Boolean = false, onClick: () -> Unit) {
    val w = if (fullWidth) "w-full" else ""
    Button(attrs = {
        classes(w, "px-5", "py-2.5", "bg-amber-500", "hover:bg-amber-600",
            "text-white", "font-semibold", "rounded-xl", "transition-colors",
            "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2")
        onClick { onClick() }
        if (loading) disabled()
    }) {
        if (loading) Spinner() else Text(text)
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit) {
    Button(attrs = {
        classes("px-4", "py-2", "border", "border-slate-300", "text-slate-700",
            "rounded-xl", "text-sm", "hover:bg-slate-50", "transition-colors")
        onClick { onClick() }
    }) { Text(text) }
}

@Composable
fun InputField(
    label: String,
    value: String,
    placeholder: String = "",
    type: String = "text",
    required: Boolean = false,
    onInput: (String) -> Unit
) {
    Div(attrs = { classes("flex", "flex-col", "gap-1") }) {
        Label(attrs = { classes("text-sm", "font-medium", "text-slate-700") }) { Text(label) }
        Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
            classes("w-full", "px-4", "py-2.5", "border", "border-slate-300", "rounded-lg",
                "focus:outline-none", "focus:ring-2", "focus:ring-amber-400",
                "focus:border-transparent", "text-sm")
            attr("type", type)
            attr("placeholder", placeholder)
            attr("value", value)
            if (required) required()
            onInput { event -> onInput(event.value) }
        })
    }
}

@Composable
fun TextArea(
    label: String,
    value: String,
    placeholder: String = "",
    rows: Int = 4,
    onInput: (String) -> Unit
) {
    val v = value
    Div(attrs = { classes("flex", "flex-col", "gap-1") }) {
        Label(attrs = { classes("text-sm", "font-medium", "text-slate-700") }) { Text(label) }
        org.jetbrains.compose.web.dom.TextArea(attrs = {
            classes("w-full", "px-4", "py-2.5", "border", "border-slate-300", "rounded-lg",
                "focus:outline-none", "focus:ring-2", "focus:ring-amber-400", "resize-none", "text-sm")
            attr("placeholder", placeholder)
            attr("rows", rows.toString())
            value(v)
            onInput { event -> onInput(event.value) }
        })
    }
}

@Composable
fun PageHeader(title: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Div(attrs = { classes("flex", "items-center", "justify-between", "mb-6") }) {
        H1(attrs = { classes("text-2xl", "font-bold", "text-slate-800") }) { Text(title) }
        if (actionLabel != null && onAction != null) {
            PrimaryButton(actionLabel, onClick = onAction)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, emoji: String, bg: String = "bg-amber-50") {
    Div(attrs = { classes("bg-white", "rounded-2xl", "p-5", "shadow-sm") }) {
        Div(attrs = { classes("inline-flex", "p-3", bg, "rounded-xl", "mb-3") }) {
            Span(attrs = { classes("text-xl") }) { Text(emoji) }
        }
        P(attrs = { classes("text-3xl", "font-bold", "text-slate-800") }) { Text(value) }
        P(attrs = { classes("text-sm", "text-slate-500", "mt-1") }) { Text(label) }
    }
}
