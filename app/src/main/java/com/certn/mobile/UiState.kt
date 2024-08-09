package com.certn.mobile

sealed class UiState<out T : Any> {
    data class Success<out T : Any>(val data: T, val callNumber: Int? = null) : UiState<T>()
    data class Error(val exception: Throwable, val callNumber: Int? = null) : UiState<Nothing>()
    data class Loading(val value: Boolean) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}
