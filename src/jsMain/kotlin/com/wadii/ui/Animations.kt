package com.wadii.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

enum class AnimEnter { FadeIn, SlideUp, SlideDown, SlideLeft, SlideRight, Scale }

/**
 * Like AnimatedVisibility in Jetpack Compose.
 * Animates content in on [visible]=true and out before removing it from the DOM on [visible]=false.
 *
 * Usage:
 *   AnimatedVisibility(visible = state is UiState.Loading) { LoadingScreen() }
 */
@Composable
fun AnimatedVisibility(
    visible: Boolean,
    enter: AnimEnter = AnimEnter.FadeIn,
    durationMs: Int = 300,
    content: @Composable () -> Unit
) {
    var shouldRender by remember { mutableStateOf(false) }
    var animVisible by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            shouldRender = true
            delay(16) // let DOM mount before triggering CSS transition
            animVisible = true
        } else {
            animVisible = false
            delay(durationMs.toLong() + 16) // wait for exit animation to finish
            shouldRender = false
        }
    }

    if (shouldRender) {
        Div(attrs = {
            style {
                property("transition", "opacity ${durationMs}ms ease, transform ${durationMs}ms ease")
                property("opacity", if (animVisible) "1" else "0")
                when (enter) {
                    AnimEnter.FadeIn -> {}
                    AnimEnter.SlideUp ->
                        property("transform", if (animVisible) "translateY(0)" else "translateY(16px)")
                    AnimEnter.SlideDown ->
                        property("transform", if (animVisible) "translateY(0)" else "translateY(-16px)")
                    AnimEnter.SlideLeft ->
                        property("transform", if (animVisible) "translateX(0)" else "translateX(16px)")
                    AnimEnter.SlideRight ->
                        property("transform", if (animVisible) "translateX(0)" else "translateX(-16px)")
                    AnimEnter.Scale ->
                        property("transform", if (animVisible) "scale(1)" else "scale(0.96)")
                }
            }
        }) {
            content()
        }
    }
}

/**
 * Animates content in when it first enters composition (enter animation only, no exit).
 * Drop this around any composable to give it a mount animation.
 *
 * Usage:
 *   is UiState.Loading -> EnterAnimation { LoadingScreen() }
 *   is UiState.Success -> EnterAnimation(AnimEnter.SlideUp, delayMs = 50) { SuccessContent() }
 */
@Composable
fun EnterAnimation(
    enter: AnimEnter = AnimEnter.FadeIn,
    durationMs: Int = 300,
    delayMs: Int = 0,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs.toLong())
        delay(16)
        visible = true
    }

    Div(attrs = {
        style {
            property("transition", "opacity ${durationMs}ms ease, transform ${durationMs}ms ease")
            property("opacity", if (visible) "1" else "0")
            when (enter) {
                AnimEnter.FadeIn -> {}
                AnimEnter.SlideUp ->
                    property("transform", if (visible) "translateY(0)" else "translateY(16px)")
                AnimEnter.SlideDown ->
                    property("transform", if (visible) "translateY(0)" else "translateY(-16px)")
                AnimEnter.SlideLeft ->
                    property("transform", if (visible) "translateX(0)" else "translateX(16px)")
                AnimEnter.SlideRight ->
                    property("transform", if (visible) "translateX(0)" else "translateX(-16px)")
                AnimEnter.Scale ->
                    property("transform", if (visible) "scale(1)" else "scale(0.96)")
            }
        }
    }) {
        content()
    }
}
