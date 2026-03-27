/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.jetnews.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.successOr
import com.example.jetnews.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostUiState(
    val post: Post? = null,
    val initialFirstVisibleItemIndex: Int = 0,
    val initialFirstVisibleItemScrollOffset: Int = 0,
    val isFavorite: Boolean = false,
    val loading: Boolean = false,
)

class PostViewModel(private val postsRepository: PostsRepository, private val postId: String) : ViewModel() {
    private val _uiState = MutableStateFlow(PostUiState(loading = true))
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val post = postsRepository.getPost(postId).successOr(null)

            _uiState.update {
                it.copy(
                    post = post,
                    loading = false,
                )
            }
        }

        viewModelScope.launch {
            postsRepository.observeFavorites().collect { favorites ->
                _uiState.update { it.copy(isFavorite = favorites.contains(postId)) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch { postsRepository.toggleFavorite(postId) }
    }

    fun onScroll(index: Int, offset: Int) {
        _uiState.update {
            it.copy(
                initialFirstVisibleItemIndex = index,
                initialFirstVisibleItemScrollOffset = offset,
            )
        }
    }

    /**
     * Factory for PostViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(postsRepository: PostsRepository, postId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PostViewModel(postsRepository, postId) as T
                }
            }
    }
}
