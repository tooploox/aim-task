package com.tooploox.aimtask;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.tooploox.aimtask.base.OneOffFlag;
import com.tooploox.aimtask.base.Status;
import com.tooploox.aimtask.domain.Schedulers;
import com.tooploox.aimtask.domain.UseCaseFactory;
import com.tooploox.aimtask.domain.entity.StationInfo;
import com.tooploox.aimtask.domain.entity.Track;

import org.threeten.bp.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;

/**
 * The presentation layer isn't ideal, as there are lots of rough edges -- tracks aren't diffed, it's not specified where loading should
 * be indicated etc. For a simple example it's enough, but for a real application I'd want this more reactive (with view events coming from
 * and observable) and less stateful, by using Data Binding for example.
 * <p>
 * There is no error handling or empty views handling, as it'd mostly just add to the layouts, activity and
 * view model without much benefit from the exercise standpoint.
 */
public class MainActivityViewModel extends ViewModel implements TracksListDataSource {

    private static final String DURATION_FORMAT = "%02d:%02d:%02d";

    private final UseCaseFactory useCaseFactory;
    private final Schedulers schedulers;
    private final CompositeDisposable compositeDisposable;

    private final MutableLiveData<OneOffFlag> tracksListUpdated;
    private final MutableLiveData<Status<Object>> screenRefreshingStatus;
    private final MutableLiveData<StationInfo> stationInfo;

    private List<Track> tracksList;

    MainActivityViewModel(UseCaseFactory useCaseFactory, Schedulers schedulers) {
        this.useCaseFactory = useCaseFactory;
        this.schedulers = schedulers;

        compositeDisposable = new CompositeDisposable();
        tracksListUpdated = new MutableLiveData<>();
        screenRefreshingStatus = new MutableLiveData<>();
        stationInfo = new MutableLiveData<>();

        subscribeToTracksListChanges();

        subscribeToStationInfoChanges();
    }

    private void subscribeToTracksListChanges() {
        compositeDisposable.add(useCaseFactory.getTracks()
                .observeOn(schedulers.getUiScheduler())
                .subscribe(
                        tracks -> {
                            if (tracks.isPresent()) {
                                tracksList = new ArrayList<>(tracks.get());
                                tracksListUpdated.setValue(OneOffFlag.create());
                            } else {
                                refreshScreen();
                            }
                        }));
    }

    private void subscribeToStationInfoChanges() {
        compositeDisposable.add(useCaseFactory.getStationInfo()
                .observeOn(schedulers.getUiScheduler())
                .subscribe(
                        stationInfo -> {
                            if (stationInfo.isPresent()) {
                                this.stationInfo.setValue(stationInfo.get());
                            } else {
                                refreshScreen();
                            }
                        }));
    }

    public void onRefreshRequested() {
        refreshScreen();
    }

    @Override
    public int getItemCount() {
        return tracksList == null ? 0 : tracksList.size();
    }

    @Override
    public void bindViewHolder(TrackView trackView, int position) {
        Track track = tracksList.get(position);

        trackView.showTrackArtist(track.getArtist());
        trackView.showTrackTitle(track.getTitle());
        trackView.showTrackThumbnail(track.getImageUrl());
        trackView.showIsPlayingNow(track.getStatus() == Track.Status.PLAYING);

        Duration duration = track.getPlayDuration();
        trackView.showTrackLength(
                String.format(
                        Locale.getDefault(),
                        DURATION_FORMAT,
                        duration.toHours(),
                        duration.toMinutes(),
                        duration.getSeconds() % 60
                )
        );
    }

    private void refreshScreen() {
        compositeDisposable.add(useCaseFactory.refreshStationInfo()
                .subscribeOn(schedulers.getIoScheduler())
                .doOnSubscribe(disposable -> screenRefreshingStatus.setValue(Status.loading()))
                .observeOn(schedulers.getUiScheduler())
                .subscribe(result -> {
                    if (result.isSuccessful()) {
                        // Kotlin has Unit for such flags, or a new sealed class entry could easily be added to denote no-value loaded state
                        screenRefreshingStatus.setValue(Status.loaded(new Object()));
                    } else {
                        //noinspection ConstantConditions
                        screenRefreshingStatus.setValue(Status.error(result.getError().getMessage()));
                    }
                }));
    }

    public MutableLiveData<OneOffFlag> getTracksListUpdated() {
        return tracksListUpdated;
    }

    public LiveData<Status<Object>> getScreenRefreshingStatus() {
        return screenRefreshingStatus;
    }

    public MutableLiveData<StationInfo> getStationInfo() {
        return stationInfo;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
