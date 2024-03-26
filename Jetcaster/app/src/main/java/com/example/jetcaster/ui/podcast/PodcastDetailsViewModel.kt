package com.example.jetcaster.ui.podcast

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.jetcaster.core.data.di.Graph
import com.example.jetcaster.core.data.model.EpisodeInfo
import com.example.jetcaster.core.data.model.PlayerEpisode
import com.example.jetcaster.core.data.model.PodcastInfo
import com.example.jetcaster.core.data.model.asExternalModel
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.player.EpisodePlayer
import com.example.jetcaster.ui.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PodcastUiState(
    val podcast: PodcastInfo = PodcastInfo(),
    val isSubscribed: Boolean = false,
    val episodes: List<EpisodeInfo> = emptyList()
)

/**
 * ViewModel that handles the business logic and screen state of the Podcast details screen.
 */
class PodcastDetailsViewModel(
    private val episodeStore: EpisodeStore = Graph.episodeStore,
    private val episodePlayer: EpisodePlayer = Graph.episodePlayer,
    private val podcastStore: PodcastStore = Graph.podcastStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val podcastUri: String =
        Uri.decode(savedStateHandle.get<String>(Screen.ARG_PODCAST_URI)!!)

    val state: StateFlow<PodcastUiState> =
        combine(
            podcastStore.podcastWithExtraInfo(podcastUri),
            episodeStore.episodesInPodcast(podcastUri)
        ) { podcast, episodeToPodcasts ->
            val episodes = episodeToPodcasts.map { it.episode.asExternalModel() }
            PodcastUiState(
                podcast = podcast.podcast.asExternalModel(),
                isSubscribed = podcast.isFollowed,
                episodes = episodes,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PodcastUiState()
        )

    fun toggleSusbcribe(podcast: PodcastInfo) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcast.uri)
        }
    }

    fun onQueueEpisode(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }

    /**
     * Factory for [PodcastDetailsViewModel].
     */
    companion object {
        fun provideFactory(
            episodeStore: EpisodeStore = Graph.episodeStore,
            podcastStore: PodcastStore = Graph.podcastStore,
            episodePlayer: EpisodePlayer = Graph.episodePlayer,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return PodcastDetailsViewModel(
                        episodeStore = episodeStore,
                        episodePlayer = episodePlayer,
                        podcastStore = podcastStore,
                        savedStateHandle = handle
                    ) as T
                }
            }
    }
}
