package com.tooploox.aimtask.domain.entity.common;

import io.reactivex.annotations.NonNull;

public interface Function<T, R> {

    R apply(@NonNull T t);
}
