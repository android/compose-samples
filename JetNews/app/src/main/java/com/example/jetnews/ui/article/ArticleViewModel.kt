/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui.article

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Article screen
 */
data class ArticleUiState(
    val post: Post? = null,
    val isFavorite: Boolean = false,
    val loading: Boolean = false
) {
    /**
     * True if the post couldn't be found
     */
    val failedLoading: Boolean
        get() = !loading && post == null
}

class ArticleViewModel(
    private val postsRepository: PostsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: String = savedStateHandle.get<String>(ARTICLE_ID_KEY)!!

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(ArticleUiState(loading = true))
    val uiState: StateFlow<ArticleUiState> = _uiState.asStateFlow()

    init {
        // Load post
        viewModelScope.launch {
            val postResult = postsRepository.getPost(postId)
            _uiState.update {
                when (postResult) {
                    is Result.Success -> it.copy(post = postResult.data, loading = false)
                    is Result.Error -> it.copy(loading = false)
                }
            }
        }

        // Update whether the post is favorite or not
        viewModelScope.launch {
            postsRepository.observeFavorites().collect { favorites ->
                _uiState.update { it.copy(isFavorite = favorites.contains(postId)) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            postsRepository.toggleFavorite(postId)
        }
    }

    /**
     * Factory for ArticleViewModel that takes PostsRepository as a dependency
     */
    companion object {
        const val ARTICLE_ID_KEY = "postId"

        fun provideFactory(
            postsRepository: PostsRepository,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return ArticleViewModel(postsRepository, handle) as T
                }
            }
    }
}
