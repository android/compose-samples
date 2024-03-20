package com.example.jetcaster.core.data.repository

import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestEpisodeStore : EpisodeStore {

    private val episodesFlow = MutableStateFlow<List<Episode>>(listOf())
    override fun episodeWithUri(episodeUri: String): Flow<Episode> =
        episodesFlow.map {
            it.first { it.uri == episodeUri }
        }

    override fun episodesInPodcast(podcastUri: String, limit: Int): Flow<List<EpisodeToPodcast>> =
        episodesFlow.map { episodes ->
            episodes.filter {
                it.podcastUri == podcastUri
            }.map { e ->
                EpisodeToPodcast().apply {
                    episode = e
                }
            }
        }

    override suspend fun addEpisodes(episodes: Collection<Episode>) =
        episodesFlow.update {
            it + episodes
        }

    override suspend fun isEmpty(): Boolean =
        episodesFlow.first().isEmpty()
}
