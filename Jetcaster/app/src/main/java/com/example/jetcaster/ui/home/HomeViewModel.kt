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

package com.example.jetcaster.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.data.PodcastStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())

    val state: StateFlow<HomeViewState>
        get() = _state

    init {
        viewModelScope.launch {
            PodcastStore.stream(StoreRequest.cached(Unit, refresh = true))
                .map { response ->
                    when (response) {
                        is StoreResponse.Data -> {
                            HomeViewState(podcasts = response.value)
                        }
                        is StoreResponse.Loading -> {
                            HomeViewState(refreshing = true)
                        }
                        is StoreResponse.Error -> {
                            HomeViewState(errorMessage = response.errorMessageOrNull())
                        }
                    }
                }.collect { _state.value = it }
        }
    }

    fun refresh() {
        fetchPodcasts()
    }

    private fun fetchPodcasts() {
        viewModelScope.launch {
            PodcastStore.fresh(Unit)
        }
    }
}

data class HomeViewState(
    val podcasts: List<Podcast> = emptyList(),
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)
