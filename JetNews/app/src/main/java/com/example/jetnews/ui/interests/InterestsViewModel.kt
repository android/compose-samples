/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.interests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.ui.state.UiState
import com.example.jetnews.ui.state.copyWithResult
import kotlinx.coroutines.launch

/**
 * ViewModel for interests screen
 */
class InterestsViewModel(private val interestsRepository: InterestsRepository) : ViewModel() {

    private val _topics = MutableLiveData<UiState<Map<String, List<String>>>>(UiState())

    /**
     * state: List of topics to display, sectioned by category
     */
    val topics: LiveData<UiState<Map<String, List<String>>>> = _topics

    /**
     * state: The currently selected topics as a Flow
     */
    val selectedTopics = interestsRepository.getTopicsSelected()

    private val _people = MutableLiveData<UiState<List<String>>>(UiState())

    /**
     * state: The list of people to display
     */
    val people: LiveData<UiState<List<String>>> = _people

    /**
     * state: The currently selected people
     */
    val selectedPeople = interestsRepository.getPeopleSelected()

    private val _publications = MutableLiveData<UiState<List<String>>>(UiState())

    /**
     * state: List of publications to display
     */
    val publications: LiveData<UiState<List<String>>> = _publications

    /**
     * state: The currently selected publications
     */
    val selectedPublications = interestsRepository.getPublicationSelected()

    init {
        viewModelScope.launch {
            refreshTopics()
            refreshPeople()
            refreshPublications()
        }
    }

    /**
     * Event: User has selected a topic
     */
    fun onTopicSelect(topic: TopicSelection) {
        viewModelScope.launch {
            interestsRepository.toggleTopicSelection(topic)
        }
    }

    /**
     * Event: User has selected a person
     */
    fun onPersonSelect(person: String) {
        viewModelScope.launch {
            interestsRepository.togglePersonSelected(person)
        }
    }

    /**
     * Event: User has selected a publication
     */
    fun onPublicationSelect(publication: String) {
        viewModelScope.launch {
            interestsRepository.togglePublicationSelected(publication)
        }
    }

    private suspend fun refreshTopics() {
        val result = interestsRepository.getTopics()
        _topics.value = _topics.value?.copyWithResult(result)
    }

    private suspend fun refreshPeople() {
        val result = interestsRepository.getPeople()
        _people.value = _people.value?.copyWithResult(result)
    }

    private suspend fun refreshPublications() {
        val result = interestsRepository.getPublications()
        _publications.value = _publications.value?.copyWithResult(result)
    }
}

class InterestsViewModelFactory(
    private val interestsRepository: InterestsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return InterestsViewModel(interestsRepository) as T
    }
}
