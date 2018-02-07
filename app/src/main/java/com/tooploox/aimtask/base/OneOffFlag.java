package com.tooploox.aimtask.base;

/**
 * Wraps a flag that once read reset its state. Should be used for one-off events from view model, like showing a toast.
 */
public class OneOffFlag {

    private boolean isSet = true;

    public static OneOffFlag create() {
        return new OneOffFlag();
    }

    public void runIfSet(Runnable runnable) {
        if (isSet) {
            isSet = false;
            runnable.run();
        }
    }
}
