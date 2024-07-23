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

package com.example.jetcaster.core.data.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import com.example.jetcaster.core.data.BuildConfig
import com.example.jetcaster.core.data.Dispatcher
import com.example.jetcaster.core.data.JetcasterDispatchers
import com.example.jetcaster.core.data.database.JetcasterDatabase
import com.example.jetcaster.core.data.database.dao.CategoriesDao
import com.example.jetcaster.core.data.database.dao.EpisodesDao
import com.example.jetcaster.core.data.database.dao.PodcastCategoryEntryDao
import com.example.jetcaster.core.data.database.dao.PodcastFollowedEntryDao
import com.example.jetcaster.core.data.database.dao.PodcastsDao
import com.example.jetcaster.core.data.database.dao.TransactionRunner
import com.example.jetcaster.core.data.repository.CategoryStore
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.LocalCategoryStore
import com.example.jetcaster.core.data.repository.LocalEpisodeStore
import com.example.jetcaster.core.data.repository.LocalPodcastStore
import com.example.jetcaster.core.data.repository.PodcastStore
import com.rometools.rome.io.SyndFeedInput
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
        .apply {
            if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
        }
        .build()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): JetcasterDatabase =
        Room.databaseBuilder(context, JetcasterDatabase::class.java, "data.db")
            // This is not recommended for normal apps, but the goal of this sample isn't to
            // showcase all of Room.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = ImageLoader.Builder(context)
        // Disable `Cache-Control` header support as some podcast images disable disk caching.
        .respectCacheHeaders(false)
        .build()

    @Provides
    @Singleton
    fun provideCategoriesDao(
        database: JetcasterDatabase
    ): CategoriesDao = database.categoriesDao()

    @Provides
    @Singleton
    fun providePodcastCategoryEntryDao(
        database: JetcasterDatabase
    ): PodcastCategoryEntryDao = database.podcastCategoryEntryDao()

    @Provides
    @Singleton
    fun providePodcastsDao(
        database: JetcasterDatabase
    ): PodcastsDao = database.podcastsDao()

    @Provides
    @Singleton
    fun provideEpisodesDao(
        database: JetcasterDatabase
    ): EpisodesDao = database.episodesDao()

    @Provides
    @Singleton
    fun providePodcastFollowedEntryDao(
        database: JetcasterDatabase
    ): PodcastFollowedEntryDao = database.podcastFollowedEntryDao()

    @Provides
    @Singleton
    fun provideTransactionRunner(
        database: JetcasterDatabase
    ): TransactionRunner = database.transactionRunnerDao()

    @Provides
    @Singleton
    fun provideSyndFeedInput() = SyndFeedInput()

    @Provides
    @Dispatcher(JetcasterDispatchers.IO)
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(JetcasterDispatchers.Main)
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideEpisodeStore(
        episodeDao: EpisodesDao
    ): EpisodeStore = LocalEpisodeStore(episodeDao)

    @Provides
    @Singleton
    fun providePodcastStore(
        podcastDao: PodcastsDao,
        podcastFollowedEntryDao: PodcastFollowedEntryDao,
        transactionRunner: TransactionRunner,
    ): PodcastStore = LocalPodcastStore(
        podcastDao = podcastDao,
        podcastFollowedEntryDao = podcastFollowedEntryDao,
        transactionRunner = transactionRunner
    )

    @Provides
    @Singleton
    fun provideCategoryStore(
        categoriesDao: CategoriesDao,
        podcastCategoryEntryDao: PodcastCategoryEntryDao,
        podcastDao: PodcastsDao,
        episodeDao: EpisodesDao,
    ): CategoryStore = LocalCategoryStore(
        episodesDao = episodeDao,
        podcastsDao = podcastDao,
        categoriesDao = categoriesDao,
        categoryEntryDao = podcastCategoryEntryDao,
    )
}
