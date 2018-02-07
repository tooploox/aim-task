package com.tooploox.aimtask.domain.entity;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import javax.annotation.Nonnull;

public class Track {

    public enum Status {
        HISTORY,
        PLAYING
    }

    public enum Type {
        SONG,
        // ToDo: Are there any other types than a song?
    }

    @Nonnull
    private final String title;

    @Nonnull
    private final String artist;

    @Nonnull
    private final String album;

    @Nonnull
    private final LocalDateTime playTime;

    @Nonnull
    private final Duration playDuration;

    @Nonnull
    private final String imageUrl;

    @Nonnull
    private final Status status;

    @Nonnull
    private final Type type;

    public Track(@Nonnull String title, @Nonnull String artist, @Nonnull String album, @Nonnull LocalDateTime playTime,
            @Nonnull Duration playDuration, @Nonnull String imageUrl, @Nonnull Status status, @Nonnull Type type) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.playTime = playTime;
        this.playDuration = playDuration;
        this.imageUrl = imageUrl;
        this.status = status;
        this.type = type;
    }

    @Nonnull
    public String getTitle() {
        return title;
    }

    @Nonnull
    public String getArtist() {
        return artist;
    }

    @Nonnull
    public String getAlbum() {
        return album;
    }

    @Nonnull
    public LocalDateTime getPlayTime() {
        return playTime;
    }

    @Nonnull
    public Duration getPlayDuration() {
        return playDuration;
    }

    @Nonnull
    public String getImageUrl() {
        return imageUrl;
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }

    @Nonnull
    public Type getType() {
        return type;
    }
}
