package com.example.jetcaster.core.data.repository

import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestPodcastStore : PodcastStore {

    private val podcastFlow = MutableStateFlow<List<Podcast>>(listOf())
    private val followedPodcasts = mutableSetOf<String>()
    override fun podcastWithUri(uri: String): Flow<Podcast> =
        podcastFlow.map { podcasts ->
            podcasts.first { it.uri == uri }
        }

    override fun podcastsSortedByLastEpisode(limit: Int): Flow<List<PodcastWithExtraInfo>> =
        podcastFlow.map { podcasts ->
            podcasts.map { p ->
                PodcastWithExtraInfo().apply {
                    podcast = p
                    isFollowed = followedPodcasts.contains(p.uri)
                }
            }
        }

    override fun followedPodcastsSortedByLastEpisode(limit: Int): Flow<List<PodcastWithExtraInfo>> =
        podcastFlow.map { podcasts ->
            podcasts.filter {
                followedPodcasts.contains(it.uri)
            }.map { p ->
                PodcastWithExtraInfo().apply {
                    podcast = p
                    isFollowed = true
                }
            }
        }

    override suspend fun togglePodcastFollowed(podcastUri: String) {
        if (podcastUri in followedPodcasts) {
            followedPodcasts.remove(podcastUri)
        } else {
            followedPodcasts.add(podcastUri)
        }
    }

    override suspend fun unfollowPodcast(podcastUri: String) {
        followedPodcasts.remove(podcastUri)
    }

    override suspend fun addPodcast(podcast: Podcast) =
        podcastFlow.update { it + podcast }

    override suspend fun isEmpty(): Boolean =
        podcastFlow.first().isEmpty()
}
