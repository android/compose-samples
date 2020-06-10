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

import android.os.Handler
import android.os.Looper
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.compose.remember
import androidx.compose.stateFor
import androidx.core.os.postDelayed
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.clickable
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.layout.Column
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSize
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.DrawerState
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Scaffold
import androidx.ui.material.ScaffoldState
import androidx.ui.material.Snackbar
import androidx.ui.material.Surface
import androidx.ui.material.TextButton
import androidx.ui.material.TopAppBar
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.ui.AppDrawer
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.SwipeToRefreshLayout
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.navigateTo
import com.example.jetnews.ui.state.RefreshableUiState
import com.example.jetnews.ui.state.currentData
import com.example.jetnews.ui.state.loading
import com.example.jetnews.ui.state.previewDataFrom
import com.example.jetnews.ui.state.refreshableUiStateFrom
import com.example.jetnews.ui.state.refreshing
import com.example.jetnews.ui.theme.snackbarAction

@Composable
fun HomeScreen(
    postsRepository: PostsRepository,
    scaffoldState: ScaffoldState = remember { ScaffoldState() }
) {
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Home,
                closeDrawer = { scaffoldState.drawerState = DrawerState.Closed }
            )
        },
        topAppBar = {
            TopAppBar(
                title = { Text(text = "Jetnews") },
                navigationIcon = {
                    IconButton(onClick = { scaffoldState.drawerState = DrawerState.Opened }) {
                        Icon(vectorResource(R.drawable.ic_jetnews_logo))
                    }
                }
            )
        },
        bodyContent = { modifier ->
            HomeScreenContent(postsRepository, modifier)
        }
    )
}

@Composable
private fun HomeScreenContent(
    postsRepository: PostsRepository,
    modifier: Modifier = Modifier
) {
    val (postsState, refreshPosts) = refreshableUiStateFrom(postsRepository::getPosts)

    if (postsState.loading && !postsState.refreshing) {
        LoadingHomeScreen()
    } else {
        SwipeToRefreshLayout(
            refreshingState = postsState.refreshing,
            onRefresh = { refreshPosts() },
            refreshIndicator = {
                Surface(elevation = 10.dp, shape = CircleShape) {
                    CircularProgressIndicator(Modifier.preferredSize(50.dp).padding(4.dp))
                }
            }
        ) {
            HomeScreenBodyWrapper(
                modifier = modifier,
                state = postsState,
                onErrorAction = {
                    refreshPosts()
                }
            )
        }
    }
}

@Composable
private fun HomeScreenBodyWrapper(
    modifier: Modifier = Modifier,
    state: RefreshableUiState<List<Post>>,
    onErrorAction: () -> Unit
) {
    // State for showing the Snackbar error. This state will reset with the content of the lambda
    // inside stateFor each time the RefreshableUiState input parameter changes.
    // showSnackbarError is the value of the error state, use updateShowSnackbarError to update it.
    val (showSnackbarError, updateShowSnackbarError) = stateFor(state) {
        state is RefreshableUiState.Error
    }

    Stack(modifier = modifier.fillMaxSize()) {
        state.currentData?.let { posts ->
            HomeScreenBody(posts = posts)
        }
        ErrorSnackbar(
            showError = showSnackbarError,
            onErrorAction = onErrorAction,
            onDismiss = { updateShowSnackbarError(false) },
            modifier = Modifier.gravity(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun HomeScreenBody(
    posts: List<Post>,
    modifier: Modifier = Modifier
) {
    val postTop = posts[3]
    val postsSimple = posts.subList(0, 2)
    val postsPopular = posts.subList(2, 7)
    val postsHistory = posts.subList(7, 10)

    VerticalScroller(modifier = modifier) {
        HomeScreenTopSection(postTop)
        HomeScreenSimpleSection(postsSimple)
        HomeScreenPopularSection(postsPopular)
        HomeScreenHistorySection(postsHistory)
    }
}

@Composable
private fun LoadingHomeScreen() {
    Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorSnackbar(
    showError: Boolean,
    modifier: Modifier = Modifier,
    onErrorAction: () -> Unit = { },
    onDismiss: () -> Unit = { }
) {
    if (showError) {
        // Make Snackbar disappear after 5 seconds if the user hasn't interacted with it
        onActive {
            // With coroutines, this will be cancellable
            Handler(Looper.getMainLooper()).postDelayed(5000L) {
                onDismiss()
            }
        }

        Snackbar(
            modifier = modifier.padding(16.dp),
            text = { Text("Can't update latest news") },
            action = {
                TextButton(
                    onClick = {
                        onErrorAction()
                        onDismiss()
                    },
                    contentColor = contentColor()
                ) {
                    Text(
                        text = "RETRY",
                        color = MaterialTheme.colors.snackbarAction
                    )
                }
            }
        )
    }
}

@Composable
private fun HomeScreenTopSection(post: Post) {
    ProvideEmphasis(EmphasisAmbient.current.high) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            text = "Top stories for you",
            style = MaterialTheme.typography.subtitle1
        )
    }
    PostCardTop(
        post = post,
        modifier = Modifier.clickable(onClick = { navigateTo(Screen.Article(post.id)) })
    )
    HomeScreenDivider()
}

@Composable
private fun HomeScreenSimpleSection(posts: List<Post>) {
    Column {
        posts.forEach { post ->
            PostCardSimple(post)
            HomeScreenDivider()
        }
    }
}

@Composable
private fun HomeScreenPopularSection(posts: List<Post>) {
    Column {
        ProvideEmphasis(EmphasisAmbient.current.high) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Popular on Jetnews",
                style = MaterialTheme.typography.subtitle1
            )
        }
        HorizontalScroller(modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)) {
            posts.forEach { post ->
                PostCardPopular(post, Modifier.padding(start = 16.dp))
            }
        }
        HomeScreenDivider()
    }
}

@Composable
private fun HomeScreenHistorySection(posts: List<Post>) {
    Column {
        posts.forEach { post ->
            PostCardHistory(post)
            HomeScreenDivider()
        }
    }
}

@Composable
private fun HomeScreenDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

@Preview("Home screen body")
@Composable
fun PreviewHomeScreenBody() {
    ThemedPreview {
        val posts = loadFakePosts()
        HomeScreenBody(posts)
    }
}

@Preview("Home screen, open drawer")
@Composable
private fun PreviewDrawerOpen() {
    ThemedPreview {
        HomeScreen(
            postsRepository = BlockingFakePostsRepository(ContextAmbient.current),
            scaffoldState = ScaffoldState(drawerState = DrawerState.Opened)
        )
    }
}

@Preview("Home screen dark theme")
@Composable
fun PreviewHomeScreenBodyDark() {
    ThemedPreview(darkTheme = true) {
        val posts = loadFakePosts()
        HomeScreenBody(posts)
    }
}

@Preview("Home screen, open drawer dark theme")
@Composable
private fun PreviewDrawerOpenDark() {
    ThemedPreview(darkTheme = true) {
        HomeScreen(
            postsRepository = BlockingFakePostsRepository(ContextAmbient.current),
            scaffoldState = ScaffoldState(drawerState = DrawerState.Opened)
        )
    }
}

@Composable
private fun loadFakePosts(): List<Post> {
    return previewDataFrom(BlockingFakePostsRepository(ContextAmbient.current)::getPosts)
}
