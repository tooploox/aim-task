package com.tooploox.aimtask.domain;

import java.util.concurrent.Executors;

import io.reactivex.Scheduler;

public class Schedulers {

    private final Scheduler uiScheduler;
    private final Scheduler ioScheduler;
    private final Scheduler tracksScheduler;

    public Schedulers(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
        this.ioScheduler = io.reactivex.schedulers.Schedulers.io();
        this.tracksScheduler = io.reactivex.schedulers.Schedulers.from(Executors.newSingleThreadExecutor());
    }

    /**
     * @return Platform-specific UI scheduler for handling result operations.
     */
    public Scheduler getUiScheduler() {
        return uiScheduler;
    }

    /**
     * @return General-purpose IO scheduler for background tasks using IO.
     */
    public Scheduler getIoScheduler() {
        return ioScheduler;
    }

    /**
     * Specific single-threaded executor to ensure that operations on Tracks list are scheduled, and there are no race conditions
     * e.g. related to multiple threads calling the API or the repository.
     *
     * @return Single-threaded scheduler specifically for Track-related operations.
     */
    public Scheduler getTracksListScheduler() {
        return tracksScheduler;
    }
}
