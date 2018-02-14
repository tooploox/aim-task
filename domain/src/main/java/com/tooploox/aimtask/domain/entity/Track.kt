package com.tooploox.aimtask.domain.entity

import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime

class Track(
        val title: String,
        val artist: String,
        val album: String,
        val playTime: LocalDateTime,
        val playDuration: Duration,
        val imageUrl: String,
        val status: Status,
        val type: Type
) {

    enum class Status {
        HISTORY,
        PLAYING
    }

    enum class Type {
        SONG
        // ToDo: Are there any other types than a song?
    }
}
