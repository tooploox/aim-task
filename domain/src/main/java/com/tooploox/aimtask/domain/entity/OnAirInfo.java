package com.tooploox.aimtask.domain.entity;

import java.util.List;

import javax.annotation.Nonnull;

public class OnAirInfo {

    @Nonnull
    private final StationInfo stationInfo;

    @Nonnull
    private final List<Track> tracks;

    public OnAirInfo(@Nonnull StationInfo stationInfo, @Nonnull List<Track> tracks) {
        this.stationInfo = stationInfo;
        this.tracks = tracks;
    }

    @Nonnull
    public StationInfo getStationInfo() {
        return stationInfo;
    }

    @Nonnull
    public List<Track> getTracks() {
        return tracks;
    }
}
