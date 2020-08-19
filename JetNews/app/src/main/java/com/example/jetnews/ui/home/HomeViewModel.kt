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

package com.example.jetnews.ui.home

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.ui.state.UiState
import com.example.jetnews.ui.state.copyWithResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Holds the UI state for the home screen.
 *
 * @param postsRepository data source
 */
class HomeViewModel(private val postsRepository: PostsRepository) : ViewModel() {

    private val _postUiState = MutableLiveData<UiState<List<Post>>>(UiState())
    /**
     * State: The current list to display, as well as error and loading status.
     */
    val postUiState: LiveData<UiState<List<Post>>> = _postUiState

    /**
     * State: Current favorites
     */
    val favorites: Flow<Set<String>> = postsRepository.observeFavorites()

    init {
        onPostRefresh()
    }

    /**
     * Event: Called when the UI wants to refresh posts
     */
    fun onPostRefresh() {
        viewModelScope.launch { doRefresh() }
    }

    /**
     * Event: Called when the UI wants to dismiss an error
     */
    fun onErrorDismissed() {
        _postUiState.value = _postUiState.value?.copy(exception = null)
    }

    /**
     * Event: Called when a favorite is toggled
     */
    fun onFavoriteToggled(postId: String) {
        viewModelScope.launch {
            postsRepository.toggleFavorite(postId)
        }
    }

    /**
     * Refresh the list from the repository on a coroutine.
     *
     * This function must be called from the Main thread to allow optimal LiveData updates.
     */
    @MainThread
    private suspend fun doRefresh() {
        try {
            _postUiState.value = _postUiState.value?.copy(loading = true)
            val result = postsRepository.getPosts()
            _postUiState.value = _postUiState.value?.copyWithResult(result)
        } finally {
            _postUiState.value = _postUiState.value?.copy(loading = false)
        }
    }
}

class HomeViewModelFactory(private val repository: PostsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(repository) as T
    }
}
