package com.tooploox.aimtask.domain

import io.reactivex.Scheduler
import java.util.concurrent.Executors

class Schedulers(
        /**
         * @return Platform-specific UI scheduler for handling result operations.
         */
        val uiScheduler: Scheduler
) {

    /**
     * @return General-purpose IO scheduler for background tasks using IO.
     */
    val ioScheduler: Scheduler = io.reactivex.schedulers.Schedulers.io()

    /**
     * Specific single-threaded executor to ensure that operations on Tracks list are scheduled, and there are no race conditions
     * e.g. related to multiple threads calling the API or the repository.
     *
     * @return Single-threaded scheduler specifically for Track-related operations.
     */
    val tracksListScheduler: Scheduler = io.reactivex.schedulers.Schedulers.from(Executors.newSingleThreadExecutor())
}
