package com.example.jetcaster.core.data.model

data class LibraryInfo(
    val podcast: PodcastInfo? = null,
    val episodes: List<EpisodeInfo> = emptyList()
)
