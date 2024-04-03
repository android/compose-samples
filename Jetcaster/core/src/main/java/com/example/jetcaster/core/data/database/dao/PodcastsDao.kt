/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetcaster.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import kotlinx.coroutines.flow.Flow

/**
 * [Room] DAO for [Podcast] related operations.
 */
@Dao
abstract class PodcastsDao : BaseDao<Podcast> {
    @Query("SELECT * FROM podcasts WHERE uri = :uri")
    abstract fun podcastWithUri(uri: String): Flow<Podcast>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT podcast_uri, MAX(published) AS last_episode_date
            FROM episodes
            GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = podcasts.uri
        WHERE podcasts.uri = :podcastUri
        ORDER BY datetime(last_episode_date) DESC
        """
    )
    abstract fun podcastWithExtraInfo(podcastUri: String): Flow<PodcastWithExtraInfo>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT podcast_uri, MAX(published) AS last_episode_date
            FROM episodes
            GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun podcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT episodes.podcast_uri, MAX(published) AS last_episode_date
            FROM episodes
            INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri
            WHERE category_id = :categoryId
            GROUP BY episodes.podcast_uri
        ) inner_query ON podcasts.uri = inner_query.podcast_uri
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = inner_query.podcast_uri
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun podcastsInCategorySortedByLastEpisode(
        categoryId: Long,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        INNER JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun followedPodcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts
        INNER JOIN (
            SELECT podcast_uri, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_uri
        ) episodes ON podcasts.uri = episodes.podcast_uri
        INNER JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = episodes.podcast_uri 
        WHERE podcasts.title LIKE '%' || :keyword || '%' 
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun searchPodcastByTitle(keyword: String, limit: Int): Flow<List<PodcastWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_uri IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT episodes.podcast_uri, MAX(published) AS last_episode_date
            FROM episodes
            INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri
            WHERE category_id IN (:categoryIdList)
            GROUP BY episodes.podcast_uri
        ) inner_query ON podcasts.uri = inner_query.podcast_uri
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_uri = inner_query.podcast_uri
        WHERE podcasts.title LIKE '%' || :keyword || '%' 
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun searchPodcastByTitleAndCategory(
        keyword: String,
        categoryIdList: List<Long>,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Query("SELECT COUNT(*) FROM podcasts")
    abstract suspend fun count(): Int
}
