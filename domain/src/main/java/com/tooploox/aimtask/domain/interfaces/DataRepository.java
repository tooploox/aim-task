package com.tooploox.aimtask.domain.interfaces;

import com.tooploox.aimtask.domain.entity.OnAirInfo;
import com.tooploox.aimtask.domain.entity.StationInfo;
import com.tooploox.aimtask.domain.entity.Track;
import com.tooploox.aimtask.domain.entity.common.Optional;

import java.util.List;

import io.reactivex.Observable;

/**
 * DataRepository class defines access to an application-wide data source. Data fetched from various sources should be put
 * in a repository, and all access to the data should happen through the repository.
 */
public interface DataRepository {

    /**
     * @param onAirInfo Most recent known info about playing station.
     */
    void setOnAirInfo(OnAirInfo onAirInfo);

    /**
     * Returns an observable emitting currently known most recent list of {@link Track}, or an empty value.
     * Implementations should emit last known, cached list first, and all subsequent updates.
     *
     * @return Observable with most recent known list of {@link Track}
     */
    Observable<Optional<List<Track>>> getTracks();

    /**
     * Returns an observable emitting currently known most recent station info, or an empty value.
     * Implementations should emit last known, cached list first, and all subsequent updates.
     *
     * @return Observable with most recent known station info
     */
    Observable<Optional<StationInfo>> getStationInfo();
}
