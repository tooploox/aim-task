package com.tooploox.aimtask;

interface TracksListDataSource {

    interface TrackView {

        void showTrackArtist(String artist);

        void showTrackTitle(String title);

        void showTrackLength(String length);

        void showTrackThumbnail(String thumbnailUrl);

        void showIsPlayingNow(boolean isPlayingNow);
    }

    int getItemCount();

    void bindViewHolder(TrackView trackView, int position);
}
