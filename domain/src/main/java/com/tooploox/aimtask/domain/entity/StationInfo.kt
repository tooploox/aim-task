package com.tooploox.aimtask.domain.entity

import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime

class StationInfo(
        val name: String,
        val description: String,
        val time: LocalDateTime,
        val duration: Duration,
        val presenter: String?,
        val imageUrl: String,
        val displayTime: String
)
