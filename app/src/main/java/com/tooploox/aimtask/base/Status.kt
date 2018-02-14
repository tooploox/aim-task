package com.tooploox.aimtask.base

/**
 * Encapsulates element that is being asynchronously loaded to be shown on the UI.
 */
sealed class Status<out T> {

    class Loading<out T> : Status<T>()

    class Loaded<out T>(val value: T) : Status<T>()

    class Error<out T>(val errorMessage: String) : Status<T>()
}
