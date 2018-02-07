package com.tooploox.aimtask.domain;

import com.tooploox.aimtask.domain.entity.OnAirInfo;
import com.tooploox.aimtask.domain.entity.StationInfo;
import com.tooploox.aimtask.domain.entity.Track;
import com.tooploox.aimtask.domain.entity.common.Optional;
import com.tooploox.aimtask.domain.entity.common.Result;
import com.tooploox.aimtask.domain.interfaces.DataGateway;
import com.tooploox.aimtask.domain.interfaces.DataRepository;

import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Normally use case factory would be final and hidden behind an interface, and would reside in a separate `:domain:interfaces` module,
 * together with domain entities and data-access interfaces. Because this is test project and it's small, it is a simple class instead.
 * <p>
 * Use cases almost (or almost always) return Rx observables, and these observables never emit `onError` by design. `onError` handlers
 * are equivalent to RuntimeExceptions, in that they indicate something went wrong.
 * <p>
 * Normally all use cases would be extracted to their own classes, but for a simple example it's not necessary.
 */
public class UseCaseFactory {

    private final DataGateway dataGateway;
    private final DataRepository dataRepository;
    private final Schedulers schedulers;

    public UseCaseFactory(@Nonnull DataGateway dataGateway, @Nonnull DataRepository dataRepository, Schedulers schedulers) {
        this.dataGateway = dataGateway;
        this.dataRepository = dataRepository;
        this.schedulers = schedulers;
    }

    /**
     * Refreshes tracks using remote gateway. The result only indicates whether the refreshing succeeded, and not actual tracks list.
     *
     * @return A {@link Single} that returns successful Result iff the refreshing succeeded.
     */
    public Single<Result<Void>> refreshStationInfo() {
        return Single.fromCallable(() -> {
                    Result<OnAirInfo> result = dataGateway.fetchStationInfo();

                    if (result.isSuccessful()) {
                        dataRepository.setOnAirInfo(result.getValue());

                        return Result.<Void>success();
                    } else {
                        return Result.<Void>error(result.getError());
                    }
                }
        ).subscribeOn(schedulers.getTracksListScheduler());
    }

    /**
     * Returns last known list of tracks.
     * <p>
     * This is just a repository call wrapped in a use case, since it's generally easier to remove this layer instead of add one, in case
     * we wanted to add some logic to this call, like domain-relevant items transformation.
     *
     * @return {@link Observable} emitting currently known list of tracks or an empty optional.
     */
    public Observable<Optional<List<Track>>> getTracks() {
        return dataRepository.getTracks();
    }

    public Observable<Optional<StationInfo>> getStationInfo() {
        return dataRepository.getStationInfo();
    }
}
