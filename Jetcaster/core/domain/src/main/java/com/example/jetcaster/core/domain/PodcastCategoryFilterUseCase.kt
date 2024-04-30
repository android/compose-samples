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

package com.example.jetcaster.core.domain

import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.asExternalModel
import com.example.jetcaster.core.model.asPodcastToEpisodeInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/**
 *  A use case which returns top podcasts and matching episodes in a given [Category].
 */
class PodcastCategoryFilterUseCase @Inject constructor(
    private val categoryStore: CategoryStore
) {
    operator fun invoke(category: CategoryInfo?): Flow<PodcastCategoryFilterResult> {
        if (category == null) {
            return flowOf(PodcastCategoryFilterResult())
        }

        val recentPodcastsFlow = categoryStore.podcastsInCategorySortedByPodcastCount(
            category.id,
            limit = 10
        )

        val episodesFlow = categoryStore.episodesFromPodcastsInCategory(
            category.id,
            limit = 20
        )

        // Combine our flows and collect them into the view state StateFlow
        return combine(recentPodcastsFlow, episodesFlow) { topPodcasts, episodes ->
            PodcastCategoryFilterResult(
                topPodcasts = topPodcasts.map { it.asExternalModel() },
                episodes = episodes.map { it.asPodcastToEpisodeInfo() }
            )
        }
    }
}
