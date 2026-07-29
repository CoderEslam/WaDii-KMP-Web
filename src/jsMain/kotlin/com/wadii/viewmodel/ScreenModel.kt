package com.wadii.viewmodel

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@Composable
inline fun <reified T : ScreenModel> rememberScreenModel(crossinline factory: () -> T): T =
    remember { factory() }