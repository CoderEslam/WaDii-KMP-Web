package com.wadii.viewmodel

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class ScreenModel {
    val screenModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    open fun onDispose() {}
    fun dispose() { onDispose(); screenModelScope.cancel() }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@Composable
inline fun <reified T : ScreenModel> rememberScreenModel(crossinline factory: () -> T): T {
    val model = remember { factory() }
    DisposableEffect(Unit) { onDispose { model.dispose() } }
    return model
}
