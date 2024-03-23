package com.example.jetcaster.core.data.model

import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import java.time.Duration

data class PlayerEpisode(
    val title: String = "",
    val subTitle: String = "",
    val duration: Duration? = null,
    val podcastName: String = "",
    val author: String = "",
    val summary: String = "",
    val podcastImageUrl: String = "",
)

fun EpisodeToPodcast.toPlayerEpisode() : PlayerEpisode =
    PlayerEpisode(
        title = episode.title,
        duration = episode.duration,
        podcastName = podcast.title,
        summary = episode.summary ?: "",
        podcastImageUrl = podcast.imageUrl ?: "",
    )
