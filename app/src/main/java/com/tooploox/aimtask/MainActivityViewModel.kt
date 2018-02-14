package com.tooploox.aimtask

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.tooploox.aimtask.base.OneOffFlag
import com.tooploox.aimtask.base.Status
import com.tooploox.aimtask.domain.Schedulers
import com.tooploox.aimtask.domain.UseCaseFactory
import com.tooploox.aimtask.domain.entity.StationInfo
import com.tooploox.aimtask.domain.entity.Track
import com.tooploox.aimtask.domain.entity.common.Optional
import com.tooploox.aimtask.domain.entity.common.Result
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.Locale

/**
 * The presentation layer isn't ideal, as there are lots of rough edges -- tracks aren't diffed, it's not specified where loading should
 * be indicated etc. For a simple example it's enough, but for a real application I'd want this more reactive (with view events coming from
 * and observable) and less stateful, by using Data Binding for example.
 *
 * There is no error handling or empty views handling, as it'd mostly just add to the layouts, activity and
 * view model without much benefit from the exercise standpoint.
 */
class MainActivityViewModel(
        private val useCaseFactory: UseCaseFactory,
        private val schedulers: Schedulers
) : ViewModel(), TracksListDataSource {

    companion object {

        private const val DURATION_FORMAT = "%02d:%02d:%02d"
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val screenRefreshingStatus: MutableLiveData<Status<Unit>> = MutableLiveData()

    val tracksListUpdated: MutableLiveData<OneOffFlag> = MutableLiveData()
    val stationInfo: MutableLiveData<StationInfo> = MutableLiveData()

    private var tracksList: List<Track>? = null

    override val itemCount: Int
        get() = tracksList?.size ?: 0

    init {
        subscribeToTracksListChanges()
        subscribeToStationInfoChanges()
    }

    private fun subscribeToTracksListChanges() {
        compositeDisposable += useCaseFactory.tracks
                .observeOn(schedulers.uiScheduler)
                .subscribe { tracks ->
                    when (tracks) {

                        is Optional.Value -> {
                            tracksList = tracks.value
                            tracksListUpdated.value = OneOffFlag.create()
                        }
                        is Optional.Empty -> refreshScreen()
                    }
                }
    }

    private fun subscribeToStationInfoChanges() {
        compositeDisposable += useCaseFactory.stationInfo
                .observeOn(schedulers.uiScheduler)
                .subscribe { stationInfo ->
                    when (stationInfo) {

                        is Optional.Value -> this.stationInfo.value = stationInfo.value
                        is Optional.Empty -> refreshScreen()
                    }
                }
    }

    fun onRefreshRequested() {
        refreshScreen()
    }

    override fun bindViewHolder(trackView: TracksListDataSource.TrackView, position: Int) {
        val track = tracksList!![position]

        trackView.showTrackArtist(track.artist)
        trackView.showTrackTitle(track.title)
        trackView.showTrackThumbnail(track.imageUrl)
        trackView.showIsPlayingNow(track.status === Track.Status.PLAYING)

        val duration = track.playDuration
        trackView.showTrackLength(
                String.format(
                        Locale.getDefault(),
                        DURATION_FORMAT,
                        duration.toHours(),
                        duration.toMinutes(),
                        duration.seconds % 60
                )
        )
    }

    private fun refreshScreen() {
        compositeDisposable += useCaseFactory.refreshStationInfo()
                .subscribeOn(schedulers.ioScheduler)
                .doOnSubscribe { screenRefreshingStatus.setValue(Status.Loading()) }
                .observeOn(schedulers.uiScheduler)
                .subscribe { result ->
                    when (result) {
                        is Result.Value -> screenRefreshingStatus.value = Status.Loaded(Unit)
                        is Result.Error -> screenRefreshingStatus.value = Status.Error(result.throwable.message!!)
                    }
                }
    }

    fun getScreenRefreshingStatus(): LiveData<Status<Unit>> = screenRefreshingStatus

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        add(disposable)
    }
}
