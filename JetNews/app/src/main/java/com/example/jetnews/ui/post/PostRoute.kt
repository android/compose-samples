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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.HomeKey
import com.example.jetnews.ui.navigation.DeepLinkKey
import com.example.jetnews.ui.navigation.ListDetailScene
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.serialization.Serializable

@Serializable
data class PostKey(val postId: String) : DeepLinkKey {
    override val parent = HomeKey
}

fun EntryProviderScope<NavKey>.postEntry(postsRepository: PostsRepository, isExpandedScreen: () -> Boolean, onBack: () -> Unit) {
    entry<PostKey>(
        metadata = ListDetailScene.detail(),
    ) {
        val postViewModel: PostViewModel =
            viewModel(factory = PostViewModel.provideFactory(postsRepository, it.postId), key = it.postId)

        val uiState by postViewModel.uiState.collectAsStateWithLifecycle()

        PostRoute(
            uiState = uiState,
            isExpandedScreen = isExpandedScreen(),
            onBack = onBack,
            onToggleFavorite = postViewModel::toggleFavorite,
            onScroll = postViewModel::onScroll,
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
fun PostRoute(
    uiState: PostUiState,
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onScroll: (index: Int, offset: Int) -> Unit,
) {
    if (uiState.loading) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                )
            }
        }
    } else {
        val post = uiState.post
        if (post != null) {
            val lazyListState = rememberLazyListState(
                initialFirstVisibleItemIndex = uiState.initialFirstVisibleItemIndex,
                initialFirstVisibleItemScrollOffset = uiState.initialFirstVisibleItemScrollOffset,
            )

            LaunchedEffect(lazyListState) {
                snapshotFlow {
                    Pair(
                        lazyListState.firstVisibleItemIndex,
                        lazyListState.firstVisibleItemScrollOffset,
                    )
                }
                    .debounce(50)
                    .collectLatest { (index, offset) ->
                        onScroll(index, offset)
                    }
            }

            ArticleScreen(
                post = post,
                isExpandedScreen = isExpandedScreen,
                onBack = onBack,
                isFavorite = uiState.isFavorite,
                onToggleFavorite = onToggleFavorite,
                lazyListState = lazyListState,
            )
        } else {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "Post not found")
                }
            }
        }
    }
}
