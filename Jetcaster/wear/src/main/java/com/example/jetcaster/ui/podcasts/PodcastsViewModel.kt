/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.ui.podcasts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.PodcastStore
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.model.asExternalModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PodcastsViewModel @Inject constructor(
    podcastStore: PodcastStore,
) : ViewModel() {

    val uiState: StateFlow<PodcastsScreenState> =
        podcastStore.followedPodcastsSortedByLastEpisode(limit = 10).map {
            if (it.isNotEmpty()) {
                PodcastsScreenState.Loaded(it.map(PodcastMapper::map))
            } else {
                PodcastsScreenState.Empty
            }
        }.catch {
            emit(PodcastsScreenState.Empty)
        }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PodcastsScreenState.Loading,
        )
}

object PodcastMapper {

    /**
     * Maps from [Podcast].
     */
    fun map(
        podcastWithExtraInfo: PodcastWithExtraInfo,
    ): PodcastInfo =
        podcastWithExtraInfo.asExternalModel()
}

@ExperimentalHorologistApi
sealed interface PodcastsScreenState {

    data object Loading : PodcastsScreenState

    data class Loaded(
        val podcastList: List<PodcastInfo>,
    ) : PodcastsScreenState

    data object Empty : PodcastsScreenState
}
