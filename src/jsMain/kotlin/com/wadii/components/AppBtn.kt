package com.wadii.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*

@Composable
fun AppBtn(
    text: String,
    onClick: () -> Unit,
    fullWidth: Boolean = false,
    loading: Boolean = false,
    icon: String? = null,
    variant: BtnVariant = BtnVariant.Primary
) {
    val base = "inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl font-semibold text-sm transition-all disabled:opacity-60 cursor-pointer border-none"
    val style = when (variant) {
        BtnVariant.Primary   -> "$base bg-amber-500 hover:bg-amber-600 text-white"
        BtnVariant.Secondary -> "$base bg-white border border-slate-200 text-slate-700 hover:bg-slate-50"
        BtnVariant.Danger    -> "$base bg-red-500 hover:bg-red-600 text-white"
        BtnVariant.Ghost     -> "$base bg-transparent text-amber-600 hover:bg-amber-50"
    }
    Button(attrs = {
        attr("class", "${if (fullWidth) "w-full " else ""}$style")
        onClick { if (!loading) onClick() }
        if (loading) attr("disabled", "true")
    }) {
        if (loading) {
            Span(attrs = { classes("w-4", "h-4", "border-2", "border-white", "border-t-transparent", "rounded-full", "animate-spin") }) {}
        } else {
            icon?.let { Span { Text(it) } }
            Text(text)
        }
    }
}

enum class BtnVariant { Primary, Secondary, Danger, Ghost }
