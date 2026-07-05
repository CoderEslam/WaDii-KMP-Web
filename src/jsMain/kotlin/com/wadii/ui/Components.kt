package com.wadii.ui

import androidx.compose.runtime.*
import com.wadii.state.AppState
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

// classes() adds each argument to the DOM's classList as one atomic token, so a single
// argument containing a space (e.g. "flex items-center") throws InvalidCharacterError.
fun classNames(vararg parts: String?): Array<String> =
    parts.filterNotNull().flatMap { it.split(" ") }.filter { it.isNotBlank() }.toTypedArray()

@Composable
fun Spinner() {
    Span(attrs = { classes("spinner") }) {}
}

@Composable
fun LoadingScreen() {
    Div(attrs = { classes("flex", "items-center", "justify-center", "min-h-64", "py-24") }) {
        Div(attrs = { classes("flex", "flex-col", "items-center", "gap-4") }) {
            Div(attrs = { classes("relative", "w-12", "h-12") }) {
                Div(attrs = { classes("w-12", "h-12", "border-4", "border-brand", "border-t-transparent", "rounded-full", "animate-spin") }) {}
                Div(attrs = {
                    style {
                        property("position", "absolute")
                        property("inset", "4px")
                        property("border", "2px solid transparent")
                        property("border-bottom-color", "var(--secondary)")
                        property("border-radius", "50%")
                        property("animation", "spin 1.5s linear infinite reverse")
                    }
                }) {}
            }
            P(attrs = { classes("text-body-subtle", "text-xs", "tracking-widest", "uppercase") }) { Text("Loading") }
        }
    }
}

@Composable
fun LoadingSkeletons(count: Int = 4, height: String = "h-24") {
    Div(attrs = { classes("space-y-3") }) {
        repeat(count) {
            Div(attrs = { classes("bg-surface", "shadow-neu-inset", "rounded-neu-base", height) }) {}
        }
    }
}

@Composable
fun EmptyState(icon: String, message: String) {
    Div(attrs = { classes("bg-surface", "shadow-neu-md", "rounded-neu-base", "p-12", "text-center") }) {
        P(attrs = { classes("text-5xl", "mb-3") }) { Text(icon) }
        P(attrs = { classes("text-body") }) { Text(message) }
    }
}

@Composable
fun Toast() {
    val msg = AppState.toastMessage ?: return
    val variant = AppState.toastVariant

    LaunchedEffect(msg) {
        delay(3000)
        AppState.clearToast()
    }

    val (bg, border, text, icon) = when (variant) {
        com.wadii.state.ToastVariant.Success -> arrayOf("bg-success-soft", "border-success-subtle", "text-fg-success-strong", "✓")
        com.wadii.state.ToastVariant.Warning -> arrayOf("bg-warning-soft", "border-warning-subtle", "text-fg-warning", "⚠")
        com.wadii.state.ToastVariant.Error   -> arrayOf("bg-danger-soft", "border-danger-subtle", "text-fg-danger-strong", "✕")
    }

    Div(attrs = {
        classes(
            "fixed", "top-4", "right-4", "z-50",
            "px-5", "py-3", "rounded-neu-base", "shadow-neu-lg", "border",
            "text-sm", "font-medium",
            "toast-enter", "flex", "items-center", "gap-2",
            bg, border, text
        )
    }) {
        Text("$icon  $msg")
    }
}

@Composable
fun Card(classes: String = "", content: @Composable () -> Unit) {
    Div(attrs = {
        classes("bg-surface", "border", "border-default", "rounded-neu-base", "shadow-neu-md")
        if (classes.isNotEmpty()) attr("class",
            "bg-surface border border-default rounded-neu-base shadow-neu-md $classes")
    }) { content() }
}

enum class BadgeVariant { Brand, Alternative, Gray, Danger, Success, Warning, Dark }

private data class BadgeTokens(val bg: String, val border: String, val text: String, val closeHover: String)

private fun badgeTokens(variant: BadgeVariant): BadgeTokens = when (variant) {
    BadgeVariant.Brand       -> BadgeTokens("bg-brand-softer", "border-brand-subtle", "text-fg-brand-strong", "hover:bg-brand-soft")
    BadgeVariant.Alternative -> BadgeTokens("bg-surface", "border-default", "text-heading", "hover:bg-surface-tertiary")
    BadgeVariant.Gray        -> BadgeTokens("bg-surface-secondary-medium", "border-default", "text-heading", "hover:bg-surface-quaternary")
    BadgeVariant.Danger      -> BadgeTokens("bg-danger-soft", "border-danger-subtle", "text-fg-danger-strong", "hover:bg-danger-medium")
    BadgeVariant.Success     -> BadgeTokens("bg-success-soft", "border-success-subtle", "text-fg-success-strong", "hover:bg-success-medium")
    BadgeVariant.Warning     -> BadgeTokens("bg-warning-soft", "border-warning-subtle", "text-fg-warning", "hover:bg-warning-medium")
    BadgeVariant.Dark        -> BadgeTokens("bg-dark", "border-transparent", "text-white", "hover:bg-dark-strong")
}

@Composable
fun Badge(
    text: String,
    variant: BadgeVariant = BadgeVariant.Brand,
    pill: Boolean = false,
    large: Boolean = false,
    icon: String? = null,
    onDismiss: (() -> Unit)? = null
) {
    val t = badgeTokens(variant)
    Span(attrs = {
        classes(
            t.bg, t.border, t.text, "border", "inline-flex", "items-center",
            if (large) "gap-1.5" else "gap-1",
            if (large) "text-sm" else "text-xs", "font-medium",
            if (large) "px-2" else "px-2.5",
            if (large) "py-1" else "py-0.5",
            if (pill) "rounded-full" else "rounded-neu-default"
        )
    }) {
        if (icon != null) Span(attrs = { classes(if (large) "text-sm" else "text-xs") }) { Text(icon) }
        Text(text)
        if (onDismiss != null) {
            Button(attrs = {
                classes("inline-flex", "items-center", "justify-center", "rounded-full", "transition-colors", t.closeHover)
                style {
                    property("background", "none"); property("border", "none"); property("cursor", "pointer")
                    property("padding", "1px"); property("color", "inherit"); property("line-height", "1")
                    property("font-size", if (large) "12px" else "10px")
                }
                onClick { onDismiss() }
            }) { Text("✕") }
        }
    }
}

private val neuButtonTextColor = mapOf(
    "brand" to "text-fg-brand",
    "secondary" to "text-body",
    "success" to "text-fg-success",
    "danger" to "text-fg-danger",
    "ghost" to "text-body"
)

@Composable
private fun neuButtonBase(
    text: String,
    textColorClass: String,
    ghost: Boolean,
    loading: Boolean,
    fullWidth: Boolean,
    icon: String?,
    disabledState: Boolean,
    onClick: () -> Unit
) {
    Button(attrs = {
        classes(*classNames(
            if (fullWidth) "w-full" else null, "px-5", "py-2.5", "bg-surface", "border", "border-default",
            textColorClass, "font-medium", "rounded-neu-base",
            if (ghost) "hover:shadow-neu-sm" else "shadow-neu-sm hover:shadow-neu-md",
            "active:shadow-neu-inset", "transition-all",
            "disabled:opacity-60", "disabled:cursor-not-allowed",
            "flex", "items-center", "justify-center", "gap-2"
        ))
        onClick { onClick() }
        if (loading || disabledState) disabled()
    }) {
        if (loading) {
            Spinner()
        } else {
            if (icon != null) Span(attrs = { classes("text-base") }) { Text(icon) }
            Text(text)
        }
    }
}

@Composable
fun PrimaryButton(
    text: String, loading: Boolean = false, fullWidth: Boolean = false,
    icon: String? = null, disabled: Boolean = false, onClick: () -> Unit
) = neuButtonBase(text, neuButtonTextColor.getValue("brand"), ghost = false, loading, fullWidth, icon, disabled, onClick)

@Composable
fun SecondaryButton(
    text: String, loading: Boolean = false, fullWidth: Boolean = false,
    icon: String? = null, disabled: Boolean = false, onClick: () -> Unit
) = neuButtonBase(text, neuButtonTextColor.getValue("secondary"), ghost = false, loading, fullWidth, icon, disabled, onClick)

@Composable
fun SuccessButton(
    text: String, loading: Boolean = false, fullWidth: Boolean = false,
    icon: String? = null, disabled: Boolean = false, onClick: () -> Unit
) = neuButtonBase(text, neuButtonTextColor.getValue("success"), ghost = false, loading, fullWidth, icon, disabled, onClick)

@Composable
fun DangerButton(
    text: String, loading: Boolean = false, fullWidth: Boolean = false,
    icon: String? = null, disabled: Boolean = false, onClick: () -> Unit
) = neuButtonBase(text, neuButtonTextColor.getValue("danger"), ghost = false, loading, fullWidth, icon, disabled, onClick)

@Composable
fun GhostButton(
    text: String, loading: Boolean = false, fullWidth: Boolean = false,
    icon: String? = null, disabled: Boolean = false, onClick: () -> Unit
) = neuButtonBase(text, neuButtonTextColor.getValue("ghost"), ghost = true, loading, fullWidth, icon, disabled, onClick)

@Composable
fun InputField(
    label: String,
    value: String,
    placeholder: String = "",
    type: String = "text",
    required: Boolean = false,
    leadingIcon: String? = null,
    trailingIcon: String? = null,
    onInput: (String) -> Unit
) {
    Div(attrs = { classes("flex", "flex-col", "gap-2") }) {
        Label(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text(label) }
        Div(attrs = { classes("relative") }) {
            if (leadingIcon != null) {
                Span(attrs = {
                    classes("absolute", "left-3", "top-1/2", "-translate-y-1/2", "text-body", "text-sm", "pointer-events-none")
                }) { Text(leadingIcon) }
            }
            if (trailingIcon != null) {
                Span(attrs = {
                    classes("absolute", "right-3", "top-1/2", "-translate-y-1/2", "text-body", "text-sm", "pointer-events-none")
                }) { Text(trailingIcon) }
            }
            Input(type = org.jetbrains.compose.web.attributes.InputType.Text, attrs = {
                classes(*classNames("w-full", "py-2.5", "border", "border-default-medium", "rounded-neu-base",
                    "bg-surface", "shadow-neu-inset", "text-sm", "text-heading",
                    if (leadingIcon != null) "pl-9" else "px-4",
                    if (trailingIcon != null) "pr-9" else null,
                    "focus:outline-none", "focus:ring-1", "focus:ring-brand",
                    "focus:border-brand"))
                attr("type", type)
                attr("placeholder", placeholder)
                value(value)
                if (required) required()
                onInput { event -> onInput(event.value) }
            })
        }
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
    Div(attrs = { classes("flex", "flex-col", "gap-2") }) {
        Label(attrs = { classes("text-sm", "font-medium", "text-heading") }) { Text(label) }
        org.jetbrains.compose.web.dom.TextArea(attrs = {
            classes("w-full", "px-4", "py-2.5", "border", "border-default-medium", "rounded-neu-base",
                "bg-surface", "shadow-neu-inset", "text-sm", "text-heading",
                "focus:outline-none", "focus:ring-1", "focus:ring-brand", "focus:border-brand", "resize-none")
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
        H1(attrs = { classes("text-2xl", "font-semibold", "text-heading") }) { Text(title) }
        if (actionLabel != null && onAction != null) {
            PrimaryButton(actionLabel, onClick = onAction)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, emoji: String, bg: String = "bg-brand-softer", onClick: (() -> Unit)? = null) {
    Div(attrs = {
        classes(*classNames(
            "bg-surface", "rounded-neu-base", "p-5", "transition-all",
            if (onClick != null) "shadow-neu-sm hover:shadow-neu-md active:shadow-neu-inset cursor-pointer" else "shadow-neu-sm"
        ))
        if (onClick != null) onClick { onClick() }
    }) {
        Div(attrs = { classes("inline-flex", "p-3", bg, "rounded-neu-base", "mb-3") }) {
            Span(attrs = { classes("text-xl") }) { Text(emoji) }
        }
        P(attrs = { classes("text-3xl", "font-bold", "text-heading") }) { Text(value) }
        P(attrs = { classes("text-xs", "text-body-subtle", "mt-1", "uppercase", "tracking-wide") }) { Text(label) }
    }
}

@Composable
fun BackButton(text: String = "", showBackArrow: Boolean = true, onClick: () -> Unit = {}) {
    Button(attrs = {
        classes(
            "inline-flex", "items-center", "gap-2", "rounded-neu-base", "bg-surface",
            "text-body", "text-sm", "font-medium", "transition-all",
            "hover:shadow-neu-sm", "hover:text-heading", "active:shadow-neu-inset"
        )
        style { property("padding", "6px 10px"); property("border", "none"); property("cursor", "pointer") }
        onClick { onClick() }
    }) {
        if (showBackArrow) {
            Span(attrs = {
                classes("inline-flex", "items-center", "justify-center")
                style { property("width", "18px"); property("height", "18px") }
                ref { element ->
                    element.innerHTML =
                        """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" width="18" height="18"><polyline points="15 18 9 12 15 6"/></svg>"""
                    onDispose {}
                }
            })
        }
        if (text.isNotEmpty()) Span { Text(text) }
    }
}
