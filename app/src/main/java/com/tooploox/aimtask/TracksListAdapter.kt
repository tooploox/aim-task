package com.tooploox.aimtask

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_track.view.*

/**
 * This adapter uses delegate patter to extract knowledge of the data outside. This can be done better with Kotlin and observable fields,
 * or using Android DataBinding's ObservableList, but since it's an exercise and Java, I sticked to the solution I'm most familiar with.
 */
class TracksListAdapter(
        context: Context,
        private val tracksListDataSource: TracksListDataSource
) : RecyclerView.Adapter<TracksListAdapter.TrackViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), TracksListDataSource.TrackView {

        private val tvTrackArtist: TextView = itemView.tvTrackArtist
        private val tvTrackTitle: TextView = itemView.tvTrackTitle
        private val tvTrackLength: TextView = itemView.tvTrackLength
        private val ivTrackImage: ImageView = itemView.ivTrackImage
        private val ivPlayingNow: ImageView = itemView.ivPlayingNow

        override fun showTrackArtist(artist: String) {
            tvTrackArtist.text = artist
        }

        override fun showTrackTitle(title: String) {
            tvTrackTitle.text = title
        }

        override fun showTrackLength(length: String) {
            tvTrackLength.text = length
        }

        override fun showTrackThumbnail(thumbnailUrl: String) {
            Glide.with(itemView)
                    .load(thumbnailUrl)
                    .into(ivTrackImage)
        }

        override fun showIsPlayingNow(isPlayingNow: Boolean) {
            if (isPlayingNow) {
                ivPlayingNow.visibility = View.VISIBLE
            } else {
                ivPlayingNow.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
            TrackViewHolder(layoutInflater.inflate(R.layout.item_track, parent, false))

    override fun getItemCount(): Int = tracksListDataSource.itemCount

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        tracksListDataSource.bindViewHolder(holder, position)
    }
}
