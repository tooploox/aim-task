package com.tooploox.aimtask.base;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Encapsulates element that is being asynchronously loaded to be shown on the UI.
 * <p>
 * This would be _way_ prettier and safer with Kotlin.
 */
public class Status<T> {

    @Nullable
    private final T value;

    @Nullable
    private final String errorMessage;

    private final boolean isLoading;

    public static <T> Status<T> loading() {
        return new Status<>(null, null, true);
    }

    public static <T> Status<T> loaded(T value) {
        return new Status<>(value, null, false);
    }

    public static <T> Status<T> error(String errorMessage) {
        return new Status<>(null, errorMessage, false);
    }

    private Status(@Nullable T value, @Nullable String errorMessage, boolean isLoading) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.isLoading = isLoading;
    }

    /**
     * @return Value if this status is not loading and wraps a value, or NoSuchElementException otherwise.
     */
    @Nonnull
    public T getValue() {
        if (value == null) {
            throw new NoSuchElementException("This status object has no value");
        }

        return value;
    }

    /**
     * @return Error message if this status is not loading and wraps an error, or NoSuchElementException otherwise.
     */
    @Nonnull
    public String getErrorMessage() {
        if (errorMessage == null) {
            throw new NoSuchElementException("This status object has no error");
        }

        return errorMessage;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isSuccess() {
        return !isLoading && value != null;
    }

    public boolean isError() {
        return !isLoading && errorMessage != null;
    }
}
