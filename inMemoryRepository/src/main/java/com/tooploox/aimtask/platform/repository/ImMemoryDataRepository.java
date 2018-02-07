package com.tooploox.aimtask.platform.repository;

import com.tooploox.aimtask.domain.entity.OnAirInfo;
import com.tooploox.aimtask.domain.entity.StationInfo;
import com.tooploox.aimtask.domain.entity.Track;
import com.tooploox.aimtask.domain.entity.common.Optional;
import com.tooploox.aimtask.domain.interfaces.DataRepository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Simple in memory repository holding the data. This resides in a separate module so that when swapping implementations for e.g.
 * shared-preferences- or database-based repository we don't rely on Proguard to remove unnecessary classes.
 * <p>
 * It also makes compilation times _really_ fast.
 */
public class ImMemoryDataRepository implements DataRepository {

    /**
     * Behavior subject used to push current tracks list to subscribers. Here default value is an empty optional, but in a real repository
     * the value would be loaded from the persistence.
     */
    private final BehaviorSubject<Optional<OnAirInfo>> onAirInfoSubject = BehaviorSubject.createDefault(Optional.empty());

    private final Observable<Optional<List<Track>>> tracksListObservable = onAirInfoSubject
            .map(onAirInfo -> onAirInfo.map(OnAirInfo::getTracks));

    private final Observable<Optional<StationInfo>> stationInfoObservable = onAirInfoSubject
            .map(onAirInfo -> onAirInfo.map(OnAirInfo::getStationInfo));

    @Override
    public void setOnAirInfo(OnAirInfo onAirInfo) {
        synchronized (this) {
            onAirInfoSubject.onNext(Optional.of(onAirInfo));
        }
    }

    @Override
    public Observable<Optional<List<Track>>> getTracks() {
        return tracksListObservable;
    }

    @Override
    public Observable<Optional<StationInfo>> getStationInfo() {
        return stationInfoObservable;
    }
}
