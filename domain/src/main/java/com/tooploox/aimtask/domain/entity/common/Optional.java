package com.tooploox.aimtask.domain.entity.common;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Optional<T> {

    @Nullable
    private final T value;

    public static <T> Optional<T> empty() {
        return new Optional<>(null);
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    private Optional(@Nullable T value) {
        this.value = value;
    }

    @Nonnull
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("Optional has no value");
        }

        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public <R> Optional<R> map(Function<T, R> mapFunction) {
        if (this.isPresent()) {
            return Optional.of(mapFunction.apply(this.get()));
        } else {
            return Optional.empty();
        }
    }
}
