package com.tooploox.aimtask.domain.entity.common

/**
 * Result class that wraps result of any action that can return a value or throw an error. Should be used e.g. to return values in
 * observables, so that observables don't emit `onError` by default.
 */
sealed class Result<out T> {

    class Value<out T>(val value: T) : Result<T>()

    class Error<out T>(val throwable: Throwable) : Result<T>()

    fun <R> map(function: (T) -> R): Result<R> =
            when (this) {
                is Result.Value -> Result.Value(function(value))
                is Result.Error -> Result.Error(throwable)
            }
}
