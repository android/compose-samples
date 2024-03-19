package com.example.jetcaster.core.data.domain

import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.di.Graph
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * A use case which returns all the latest episodes from all the podcasts the user follows.
 */
class GetLatestFollowedEpisodesUseCase(
    private val episodeStore: EpisodeStore = Graph.episodeStore,
    private val podcastStore: PodcastStore = Graph.podcastStore,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<EpisodeToPodcast>> =
        podcastStore.followedPodcastsSortedByLastEpisode()
            .flatMapLatest { followedPodcasts ->
                if (followedPodcasts.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(
                        followedPodcasts.map { p ->
                            episodeStore.episodesInPodcast(p.podcast.uri, 5)
                        }
                    ) { allEpisodes ->
                        allEpisodes.toList().flatten().sortedByDescending { it.episode.published }
                    }
                }
            }
}
