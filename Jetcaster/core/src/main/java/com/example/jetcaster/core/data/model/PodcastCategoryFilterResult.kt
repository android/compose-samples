package com.example.jetcaster.core.data.model

import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo

/**
 * A model holding top podcasts and matching episodes when filtering based on a category.
 */
data class PodcastCategoryFilterResult(
    val topPodcasts: List<PodcastWithExtraInfo> = emptyList(),
    val episodes: List<EpisodeToPodcast> = emptyList()
)
