package com.example.androidcasting.core.utils

/**
 * Generic wrapper for asynchronous operations. Used across data/domain layers
 * to represent success, in-progress and error states in a type safe manner.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
