package com.tooploox.aimtask.base

/**
 * Wraps a flag that once read reset its state. Should be used for one-off events from view model, like showing a toast.
 */
class OneOffFlag {

    companion object {

        @JvmStatic
        fun create(): OneOffFlag = OneOffFlag()
    }

    private var isSet = true

    fun runIfSet(block: () -> Unit) {
        if (isSet) {
            isSet = false
            block()
        }
    }
}
