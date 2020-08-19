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

package com.example.jetnews.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.ui.state.UiState
import com.example.jetnews.ui.state.copyWithResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Holds UI state for article screen
 *
 * @param postId the post to show
 * @param postsRepository data source to load posts and favorites from
 */
class ArticleViewModel(val postId: String, val postsRepository: PostsRepository) : ViewModel() {

    /**
     * State: The post to show
     */
    val post: LiveData<UiState<Post>> = liveData {
        // emit a loading state
        var state = UiState<Post>(loading = true)
        emit(state)

        // when result comes in, update state and emit the final value
        state = state.copyWithResult(postsRepository.getPost(postId))
        emit(state)
    }

    /**
     * State: The current favorites
     */
    val favorites: Flow<Set<String>> = postsRepository.observeFavorites()

    /**
     * Event: Toggle the favorite value of this post
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            postsRepository.toggleFavorite(postId)
        }
    }
}

class ArticleViewModelFactory(
    private val postId: String,
    private val postsRepository: PostsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ArticleViewModel(postId, postsRepository) as T
    }
}
