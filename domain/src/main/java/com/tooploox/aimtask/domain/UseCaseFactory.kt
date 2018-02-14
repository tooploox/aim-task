package com.tooploox.aimtask.domain

import com.tooploox.aimtask.domain.entity.StationInfo
import com.tooploox.aimtask.domain.entity.Track
import com.tooploox.aimtask.domain.entity.common.Optional
import com.tooploox.aimtask.domain.entity.common.Result
import com.tooploox.aimtask.domain.interfaces.DataGateway
import com.tooploox.aimtask.domain.interfaces.DataRepository
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Normally use case factory would be final and hidden behind an interface, and would reside in a separate `:domain:interfaces` module,
 * together with domain entities and data-access interfaces. Because this is test project and it's small, it is a simple class instead.
 *
 *
 * Use cases almost (or almost always) return Rx observables, and these observables never emit `onError` by design. `onError` handlers
 * are equivalent to RuntimeExceptions, in that they indicate something went wrong.
 *
 *
 * Normally all use cases would be extracted to their own classes, but for a simple example it's not necessary.
 */
class UseCaseFactory(
        private val dataGateway: DataGateway,
        private val dataRepository: DataRepository,
        private val schedulers: Schedulers
) {

    /**
     * Returns last known list of tracks.
     *
     *
     * This is just a repository call wrapped in a use case, since it's generally easier to remove this layer instead of add one, in case
     * we wanted to add some logic to this call, like domain-relevant items transformation.
     *
     * @return [Observable] emitting currently known list of tracks or an empty optional.
     */
    val tracks: Observable<Optional<List<Track>>>
        get() = dataRepository.tracks

    val stationInfo: Observable<Optional<StationInfo>>
        get() = dataRepository.stationInfo

    /**
     * Refreshes tracks using remote gateway. The result only indicates whether the refreshing succeeded, and not actual tracks list.
     *
     * @return A [Single] that returns successful Result iff the refreshing succeeded.
     */
    fun refreshStationInfo(): Single<Result<Void>> = Single.fromCallable<Result<Void>> {
        val result = dataGateway.fetchStationInfo()

        if (result.isSuccessful) {
            dataRepository.setOnAirInfo(result.value)

            Result.success<Void>()
        } else {
            Result.error<Void>(result.error!!)
        }
    }.subscribeOn(schedulers.tracksListScheduler)
}
