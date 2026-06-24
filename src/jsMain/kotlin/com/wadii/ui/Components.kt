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
fun LoadingScreen() {
    Div(attrs = { classes("flex", "items-center", "justify-center", "min-h-64", "py-24") }) {
        Div(attrs = { classes("flex", "flex-col", "items-center", "gap-4") }) {
            Div(attrs = { classes("relative") }) {
                Div(attrs = { classes("w-12", "h-12", "border-4", "border-amber-500", "border-t-transparent", "rounded-full", "animate-spin") }) {}
                Div(attrs = {
                    style {
                        property("position", "absolute")
                        property("inset", "4px")
                        property("border", "2px solid transparent")
                        property("border-bottom-color", "rgba(139,92,246,0.5)")
                        property("border-radius", "50%")
                        property("animation", "spin 1.5s linear infinite reverse")
                    }
                }) {}
            }
            P(attrs = { classes("text-slate-400", "text-xs", "tracking-widest", "uppercase") }) { Text("Loading") }
        }
    }
}

@Composable
fun LoadingSkeletons(count: Int = 4, height: String = "h-24") {
    Div(attrs = { classes("space-y-3") }) {
        repeat(count) {
            Div(attrs = { classes("bg-white", "rounded-2xl", "animate-pulse", height) }) {}
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
            "px-5", "py-3", "rounded-2xl", "shadow-2xl",
            "text-white", "text-sm", "font-medium",
            "toast-enter", "flex", "items-center", "gap-2",
            if (isError) "bg-red-500" else "bg-green-500"
        )
    }) {
        Text(if (isError) "✕  $msg" else "✓  $msg")
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
        "green"  -> arrayOf("bg-green-50",  "text-green-700")
        "red"    -> arrayOf("bg-red-50",    "text-red-700")
        "yellow" -> arrayOf("bg-yellow-50", "text-yellow-700")
        "blue"   -> arrayOf("bg-blue-50",   "text-blue-700")
        else     -> arrayOf("bg-amber-50",  "text-amber-700")
    }
    Span(attrs = { classes(*colors, "text-xs", "font-medium", "px-2.5", "py-0.5", "rounded-full") }) {
        Text(text)
    }
}

@Composable
fun PrimaryButton(text: String, loading: Boolean = false, fullWidth: Boolean = false, onClick: () -> Unit) {
    Button(attrs = {
        classes(
            if (fullWidth) "w-full" else "", "px-5", "py-2.5", "bg-amber-500", "hover:bg-amber-600",
            "text-white", "font-semibold", "rounded-xl", "transition-all",
            "disabled:opacity-60", "flex", "items-center", "justify-center", "gap-2"
        )
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
            "rounded-xl", "text-sm", "hover:bg-slate-50", "transition-all")
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
    Div(attrs = { classes("flex", "flex-col", "gap-1.5") }) {
        Label(attrs = { classes("text-xs", "font-semibold", "text-slate-600", "uppercase", "tracking-wide") }) { Text(label) }
        Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
            classes("w-full", "px-4", "py-2.5", "border", "border-slate-300", "rounded-xl",
                "focus:outline-none", "focus:ring-2", "focus:ring-amber-400",
                "focus:border-transparent", "text-sm", "bg-white")
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
    Div(attrs = { classes("flex", "flex-col", "gap-1.5") }) {
        Label(attrs = { classes("text-xs", "font-semibold", "text-slate-600", "uppercase", "tracking-wide") }) { Text(label) }
        org.jetbrains.compose.web.dom.TextArea(attrs = {
            classes("w-full", "px-4", "py-2.5", "border", "border-slate-300", "rounded-xl",
                "focus:outline-none", "focus:ring-2", "focus:ring-amber-400", "resize-none", "text-sm", "bg-white")
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
        P(attrs = { classes("text-xs", "text-slate-500", "mt-1", "uppercase", "tracking-wide") }) { Text(label) }
    }
}
