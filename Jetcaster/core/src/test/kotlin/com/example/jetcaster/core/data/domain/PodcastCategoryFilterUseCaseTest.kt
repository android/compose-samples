package com.example.jetcaster.core.data.domain

import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.core.data.repository.TestCategoryStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.OffsetDateTime

class PodcastCategoryFilterUseCaseTest {

    private val categoriesStore = TestCategoryStore()
    private val testEpisodeToPodcast = listOf(
        EpisodeToPodcast().apply {
            episode = Episode(
                "",
                "",
                "Episode 1",
                published = OffsetDateTime.now())
        },
        EpisodeToPodcast().apply {
            episode = Episode(
                "",
                "",
                "Episode 2",
                published = OffsetDateTime.now())
        },
        EpisodeToPodcast().apply {
            episode = Episode(
                "",
                "",
                "Episode 2",
                published = OffsetDateTime.now())
        }
    )
    private val testCategory = Category(1, "Technology")

    val useCase = PodcastCategoryFilterUseCase(
        categoryStore = categoriesStore
    )

    @Test
    fun whenCategoryNull_emptyFlow() = runTest {
        val resultFlow = useCase(null)

        categoriesStore.setEpisodesFromPodcast(testCategory.id, testEpisodeToPodcast)
        categoriesStore.setPodcastsInCategory(testCategory.id, testPodcasts)

        val result = resultFlow.first()
        assertTrue(result.topPodcasts.isEmpty())
        assertTrue(result.episodes.isEmpty())
    }

    @Test
    fun whenCategoryNotNull_validFlow() = runTest {
        val resultFlow = useCase(testCategory)

        categoriesStore.setEpisodesFromPodcast(testCategory.id, testEpisodeToPodcast)
        categoriesStore.setPodcastsInCategory(testCategory.id, testPodcasts)

        val result = resultFlow.first()
        assertEquals(
            testPodcasts,
            result.topPodcasts
        )
        assertEquals(
            testEpisodeToPodcast,
            result.episodes
        )
    }
}

val testPodcasts = listOf(
    PodcastWithExtraInfo().apply {
        podcast = Podcast(uri = "nia", title = "Now in Android")
    },
    PodcastWithExtraInfo().apply {
        podcast = Podcast(uri = "adb", title = "Android Developers Backstage")
    },
    PodcastWithExtraInfo().apply {
        podcast = Podcast(uri = "techcrunch", title = "Techcrunch")
    },
)
