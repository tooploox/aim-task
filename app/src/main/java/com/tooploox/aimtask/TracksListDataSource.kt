package com.tooploox.aimtask

interface TracksListDataSource {

    val itemCount: Int

    interface TrackView {

        fun showTrackArtist(artist: String)

        fun showTrackTitle(title: String)

        fun showTrackLength(length: String)

        fun showTrackThumbnail(thumbnailUrl: String)

        fun showIsPlayingNow(isPlayingNow: Boolean)
    }

    fun bindViewHolder(trackView: TrackView, position: Int)
}
