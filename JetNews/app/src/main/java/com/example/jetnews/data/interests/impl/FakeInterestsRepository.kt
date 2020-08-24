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

package com.example.jetnews.data.interests.impl

import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.data.interests.TopicsMap
import com.example.jetnews.utils.addOrRemove
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementation of InterestRepository that returns a hardcoded list of
 * topics, people and publications synchronously.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakeInterestsRepository : InterestsRepository {

    private val topics by lazy {
        mapOf(
            "Android" to listOf("Jetpack Compose", "Kotlin", "Jetpack"),
            "Programming" to listOf("Kotlin", "Declarative UIs", "Java"),
            "Technology" to listOf("Pixel", "Google")
        )
    }

    private val people by lazy {
        listOf(
            "Kobalt Toral",
            "K'Kola Uvarek",
            "Kris Vriloc",
            "Grala Valdyr",
            "Kruel Valaxar",
            "L'Elij Venonn",
            "Kraag Solazarn",
            "Tava Targesh",
            "Kemarrin Muuda"
        )
    }

    private val publications by lazy {
        listOf(
            "Kotlin Vibe",
            "Compose Mix",
            "Compose Breakdown",
            "Android Pursue",
            "Kotlin Watchman",
            "Jetpack Ark",
            "Composeshack",
            "Jetpack Point",
            "Compose Tribune"
        )
    }

    // for now, keep the selections in memory
    private val selectedTopics = MutableStateFlow(setOf<TopicSelection>())
    private val selectedPeople = MutableStateFlow(setOf<String>())
    private val selectedPublications = MutableStateFlow(setOf<String>())

    // Used to make suspend functions that read and update state safe to call from any thread
    private val mutex = Mutex()

    override suspend fun getTopics(): Result<TopicsMap> {
        return Result.Success(topics)
    }

    override suspend fun getPeople(): Result<List<String>> {
        return Result.Success(people)
    }

    override suspend fun getPublications(): Result<List<String>> {
        return Result.Success(publications)
    }

    override suspend fun toggleTopicSelection(topic: TopicSelection) {
        mutex.withLock {
            val set = selectedTopics.value.toMutableSet()
            set.addOrRemove(topic)
            selectedTopics.value = set
        }
    }

    override suspend fun togglePersonSelected(person: String) {
        mutex.withLock {
            val set = selectedPeople.value.toMutableSet()
            set.addOrRemove(person)
            selectedPeople.value = set
        }
    }

    override suspend fun togglePublicationSelected(publication: String) {
        mutex.withLock {
            val set = selectedPublications.value.toMutableSet()
            set.addOrRemove(publication)
            selectedPublications.value = set
        }
    }

    override fun observeTopicsSelected(): Flow<Set<TopicSelection>> = selectedTopics

    override fun observePeopleSelected(): Flow<Set<String>> = selectedPeople

    override fun observePublicationSelected(): Flow<Set<String>> = selectedPublications
}
