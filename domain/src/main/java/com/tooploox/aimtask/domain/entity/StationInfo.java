package com.tooploox.aimtask.domain.entity;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StationInfo {

    @Nonnull
    private final String name;

    @Nonnull
    private final String description;

    @Nonnull
    private final LocalDateTime time;

    @Nonnull
    private final Duration duration;

    @Nullable
    private final String presenter;

    @Nonnull
    private final String imageUrl;

    @Nonnull
    private final String displayTime;

    public StationInfo(@Nonnull String name, @Nonnull String description, @Nonnull LocalDateTime time, @Nonnull Duration duration,
            @Nullable String presenter, @Nonnull String imageUrl, @Nonnull String displayTime) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.duration = duration;
        this.presenter = presenter;
        this.imageUrl = imageUrl;
        this.displayTime = displayTime;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Nonnull
    public LocalDateTime getTime() {
        return time;
    }

    @Nonnull
    public Duration getDuration() {
        return duration;
    }

    @Nullable
    public String getPresenter() {
        return presenter;
    }

    @Nonnull
    public String getImageUrl() {
        return imageUrl;
    }

    @Nonnull
    public String getDisplayTime() {
        return displayTime;
    }
}
