package com.tooploox.aimtask.domain.entity.common

sealed class Optional<out T> {

    class Value<out T>(val value: T) : Optional<T>()

    class Empty<out T> : Optional<T>()

    fun <R> map(function: (T) -> R): Optional<R> = when (this) {
        is Optional.Value -> Optional.Value(function(value))
        is Optional.Empty -> Optional.Empty()
    }
}
