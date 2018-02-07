package com.tooploox.aimtask.domain.entity.common;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.reactivex.annotations.NonNull;

/**
 * Result class that wraps result of any action that can return a value or throw an error. Should be used e.g. to return values in
 * observables, so that observables don't emit `onError` by default.
 * <p>
 * In Kotlin this would be implemented using sealed classes, for compile-time safety as to which fields can be accessed.
 */
public class Result<T> {

    @Nullable
    private final T value;

    @Nullable
    private final Throwable error;

    public static <T> Result<T> success() {
        return new Result<>(null, null);
    }

    public static <T> Result<T> value(@NonNull T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> error(@NonNull Throwable throwable) {
        return new Result<>(null, throwable);
    }

    private Result(@Nullable T value, @Nullable Throwable error) {
        this.value = value;
        this.error = error;
    }

    @Nonnull
    public T getValue() {
        if (value == null) {
            throw new NoSuchElementException("Result has no value set");
        }

        return value;
    }

    /**
     * Returns null iff this result wraps a success (with or without a value)
     */
    @Nullable
    public Throwable getError() {
        return error;
    }

    public boolean isSuccessful() {
        return error == null;
    }

    public boolean hasValue() {
        return value != null;
    }
}
