package com.example.jetcaster.core.data.domain

import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.data.repository.CategoryStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/**
 *  A use case which returns top podcasts and matching episodes in a given [Category].
 */
class PodcastCategoryFilterUseCase(
    private val categoryStore: CategoryStore
) {
    operator fun invoke(category: Category?): Flow<PodcastCategoryFilterResult> {
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
                topPodcasts = topPodcasts,
                episodes = episodes
            )
        }
    }
}
