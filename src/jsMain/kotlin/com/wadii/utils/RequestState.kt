package com.wadii.utils


import androidx.compose.runtime.Composable

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

    @Composable
    fun DisplayResult(
        onIdle: (@Composable () -> Unit)? = null,
        onLoading: @Composable () -> Unit,
        onSuccess: @Composable (T) -> Unit,
        onError: @Composable (String) -> Unit
    ) {
        when (this) {
            is Idle -> {
                onIdle?.invoke()
            }

            is Loading -> {
                onLoading()
            }

            is Success -> {
                onSuccess(this.getSuccessData())
            }

            is Error -> {
                onError(this.getErrorMessage())
            }
        }

    }

    fun handelState(
        onIdle: () -> Unit = {},
        onLoading: () -> Unit = {},
        onSuccess: (T) -> Unit,
        onError: (String, StatusCode) -> Unit,
    ) {
        when (this) {
            is Idle -> {
                onIdle.invoke()
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