package com.tooploox.aimtask;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * This adapter uses delegate patter to extract knowledge of the data outside. This can be done better with Kotlin and observable fields,
 * or using Android DataBinding's ObservableList, but since it's an exercise and Java, I sticked to the solution I'm most familiar with.
 */
public class TracksListAdapter extends RecyclerView.Adapter<TracksListAdapter.TrackViewHolder> {

    static class TrackViewHolder extends RecyclerView.ViewHolder implements TracksListDataSource.TrackView {

        private final TextView tvTrackArtist;
        private final TextView tvTrackTitle;
        private final TextView tvTrackLength;
        private final ImageView ivTrackImage;
        private final ImageView ivPlayingNow;

        TrackViewHolder(View itemView) {
            super(itemView);

            tvTrackArtist = itemView.findViewById(R.id.tvTrackArtist);
            tvTrackTitle = itemView.findViewById(R.id.tvTrackTitle);
            tvTrackLength = itemView.findViewById(R.id.tvTrackLength);
            ivTrackImage = itemView.findViewById(R.id.ivTrackImage);
            ivPlayingNow = itemView.findViewById(R.id.ivPlayingNow);
        }

        @Override
        public void showTrackArtist(String artist) {
            tvTrackArtist.setText(artist);
        }

        @Override
        public void showTrackTitle(String title) {
            tvTrackTitle.setText(title);
        }

        @Override
        public void showTrackLength(String length) {
            tvTrackLength.setText(length);
        }

        @Override
        public void showTrackThumbnail(String thumbnailUrl) {
            Glide.with(itemView)
                    .load(thumbnailUrl)
                    .into(ivTrackImage);
        }

        @Override
        public void showIsPlayingNow(boolean isPlayingNow) {
            if (isPlayingNow) {
                ivPlayingNow.setVisibility(View.VISIBLE);
            } else {
                ivPlayingNow.setVisibility(View.INVISIBLE);
            }
        }
    }

    private final LayoutInflater layoutInflater;
    private final TracksListDataSource tracksListDataSource;

    TracksListAdapter(Context context, TracksListDataSource tracksListDataSource) {
        layoutInflater = LayoutInflater.from(context);
        this.tracksListDataSource = tracksListDataSource;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrackViewHolder(layoutInflater.inflate(R.layout.item_track, parent, false));
    }

    @Override
    public int getItemCount() {
        return tracksListDataSource.getItemCount();
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        tracksListDataSource.bindViewHolder(holder, position);
    }
}
