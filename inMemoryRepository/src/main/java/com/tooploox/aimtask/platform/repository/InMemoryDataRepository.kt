package com.tooploox.aimtask.platform.repository

import com.tooploox.aimtask.domain.entity.OnAirInfo
import com.tooploox.aimtask.domain.entity.StationInfo
import com.tooploox.aimtask.domain.entity.Track
import com.tooploox.aimtask.domain.entity.common.Optional
import com.tooploox.aimtask.domain.interfaces.DataRepository
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * Simple in memory repository holding the data. This resides in a separate module so that when swapping implementations for e.g.
 * shared-preferences- or database-based repository we don't rely on Proguard to remove unnecessary classes.
 *
 *
 * It also makes compilation times _really_ fast.
 */
class InMemoryDataRepository : DataRepository {

    /**
     * Behavior subject used to push current tracks list to subscribers. Here default value is an empty optional, but in a real repository
     * the value would be loaded from the persistence.
     */
    private val onAirInfoSubject: BehaviorSubject<Optional<OnAirInfo>> = BehaviorSubject.createDefault(Optional.Empty())

    override val tracks: Observable<Optional<List<Track>>> = onAirInfoSubject
            .map { it.map { it.tracks } }

    override val stationInfo: Observable<Optional<StationInfo>> = onAirInfoSubject
            .map { it.map { it.stationInfo } }

    override fun setOnAirInfo(onAirInfo: OnAirInfo) {
        synchronized(this) {
            onAirInfoSubject.onNext(Optional.Value(onAirInfo))
        }
    }
}
