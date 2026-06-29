package com.wadii.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class RequestState<out T>() {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val message: String, val code: StatusCode = StatusCode.Unknown) :
        RequestState<Nothing>()

    fun isLoading() = this is Loading
    fun isSuccess() = this is Success
    fun isError() = this is Error

    /**
     * Returns data from a [Success].
     * @throws ClassCastException If the current state is not [Success]
     *  */
    fun getSuccessData() = (this as Success).data
    fun getSuccessDataOrNull(): T? {
        return try {
            (this as Success).data
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Returns an error message from an [Error]
     * @throws ClassCastException If the current state is not [Error]
     *  */
    fun getErrorMessage() = (this as Error).message

    fun getErrorCode() = (this as Error).code

    fun getErrorMessageOrEmpty(): String {
        return try {
            (this as Error).message
        } catch (e: Exception) {
            ""
        }
    }

    fun exceptionOrNull(): Throwable? = when (this) {
        is Error -> Exception(message)
        else -> null
    }

    @OptIn(ExperimentalContracts::class)
    inline fun <R> getOrElse(onFailure: (exception: Throwable) -> R, onSuccess: (T) -> R): R {
        contract {
            callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
            callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        }
        return when (this) {
            is Success -> onSuccess(data)
            is Error -> onFailure(Exception(message))
            else -> onFailure(Exception("Invalid state: $this"))
        }
    }

    @Composable
    fun DisplayResult(
        onIdle: (@Composable () -> Unit)? = null,
        onLoading: @Composable () -> Unit,
        onSuccess: @Composable (T) -> Unit,
        onError: @Composable (String) -> Unit,
        transitionSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
            fadeIn(tween(durationMillis = 300)) togetherWith
                    fadeOut(tween(durationMillis = 300))
        }
    ) {
        AnimatedContent(
            targetState = this,
            transitionSpec = transitionSpec,
            label = "Animated State"
        ) { state ->
            when (state) {
                is Idle -> {
                    onIdle?.invoke()
                }

                is Loading -> {
                    onLoading()
                }

                is Success -> {
                    onSuccess(state.getSuccessData())
                }

                is Error -> {
                    onError(state.getErrorMessage())
                }
            }
        }
    }

    fun handelState(
        onIdle: () -> Unit = {},
        onLoading: () -> Unit = {},
        onSuccess: (T) -> Unit = {},
        onError: (String, StatusCode) -> Unit = { error, code -> },
    ) {
        when (this) {
            is Idle -> {
                onIdle?.invoke()
            }

            is Loading -> {
                onLoading()
            }

            is Success -> {
                onSuccess(getSuccessData())
            }

            is Error -> {
                onError(getErrorMessage(), getErrorCode())
            }
        }
    }
}


@Serializable
data class ProductDto(
    val id: Long,
    val title: String,
    val price: Double
)

@Serializable
data class ProductResponseDto(
    val products: List<ProductDto>,
    val total: Long
)