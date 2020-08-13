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

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.ui.AppDrawer
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.SwipeToRefreshLayout
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.state.RefreshableUiState
import com.example.jetnews.ui.state.currentData
import com.example.jetnews.ui.state.loading
import com.example.jetnews.ui.state.previewDataFrom
import com.example.jetnews.ui.state.refreshableUiStateFrom
import com.example.jetnews.ui.state.refreshing
import com.example.jetnews.ui.theme.snackbarAction
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navigateTo: (Screen) -> Unit,
    postsRepository: PostsRepository,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Home,
                closeDrawer = { scaffoldState.drawerState.close() },
                navigateTo = navigateTo
            )
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "Jetnews") },
                navigationIcon = {
                    IconButton(onClick = { scaffoldState.drawerState.open() }) {
                        Icon(vectorResource(R.drawable.ic_jetnews_logo))
                    }
                }
            )
        },
        bodyContent = { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            HomeScreenContent(postsRepository, navigateTo, modifier)
        }
    )
}

@Composable
private fun HomeScreenContent(
    postsRepository: PostsRepository,
    navigateTo: (Screen) -> Unit,
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
                    CircularProgressIndicator(
                        modifier = Modifier
                            .preferredSize(36.dp)
                            .padding(4.dp)
                    )
                }
            }
        ) {
            HomeScreenBodyWrapper(
                modifier = modifier,
                state = postsState,
                onErrorAction = {
                    refreshPosts()
                },
                navigateTo = navigateTo
            )
        }
    }
}

@Composable
private fun HomeScreenBodyWrapper(
    modifier: Modifier = Modifier,
    state: RefreshableUiState<List<Post>>,
    onErrorAction: () -> Unit,
    navigateTo: (Screen) -> Unit
) {
    // State for showing the Snackbar error. This state will reset with the content of the lambda
    // inside stateFor each time the RefreshableUiState input parameter changes.
    // showSnackbarError is the value of the error state, use updateShowSnackbarError to update it.
    val (showSnackbarError, updateShowSnackbarError) = remember(state) {
        mutableStateOf(state is RefreshableUiState.Error)
    }

    Stack(modifier = modifier.fillMaxSize()) {
        state.currentData?.let { posts ->
            HomeScreenBody(posts = posts, navigateTo = navigateTo)
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
    modifier: Modifier = Modifier,
    navigateTo: (Screen) -> Unit
) {
    val postTop = posts[3]
    val postsSimple = posts.subList(0, 2)
    val postsPopular = posts.subList(2, 7)
    val postsHistory = posts.subList(7, 10)

    ScrollableColumn(modifier = modifier) {
        HomeScreenTopSection(postTop, navigateTo)
        HomeScreenSimpleSection(postsSimple, navigateTo)
        HomeScreenPopularSection(postsPopular, navigateTo)
        HomeScreenHistorySection(postsHistory, navigateTo)
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
        launchInComposition {
            delay(timeMillis = 5000L)
            onDismiss()
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
private fun HomeScreenTopSection(post: Post, navigateTo: (Screen) -> Unit) {
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
private fun HomeScreenSimpleSection(
    posts: List<Post>,
    navigateTo: (Screen) -> Unit
) {
    Column {
        posts.forEach { post ->
            PostCardSimple(post, navigateTo)
            HomeScreenDivider()
        }
    }
}

@Composable
private fun HomeScreenPopularSection(
    posts: List<Post>,
    navigateTo: (Screen) -> Unit
) {
    Column {
        ProvideEmphasis(EmphasisAmbient.current.high) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Popular on Jetnews",
                style = MaterialTheme.typography.subtitle1
            )
        }
        ScrollableRow(modifier = Modifier.padding(end = 16.dp)) {
            posts.forEach { post ->
                PostCardPopular(post, navigateTo, Modifier.padding(start = 16.dp, bottom = 16.dp))
            }
        }
        HomeScreenDivider()
    }
}

@Composable
private fun HomeScreenHistorySection(
    posts: List<Post>,
    navigateTo: (Screen) -> Unit
) {
    Column {
        posts.forEach { post ->
            PostCardHistory(post, navigateTo)
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
        HomeScreenBody(posts, navigateTo = { })
    }
}

@Preview("Home screen, open drawer")
@Composable
private fun PreviewDrawerOpen() {
    ThemedPreview {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Open)
        )
        HomeScreen(
            postsRepository = BlockingFakePostsRepository(ContextAmbient.current),
            scaffoldState = scaffoldState,
            navigateTo = { }
        )
    }
}

@Preview("Home screen dark theme")
@Composable
fun PreviewHomeScreenBodyDark() {
    ThemedPreview(darkTheme = true) {
        val posts = loadFakePosts()
        HomeScreenBody(posts, navigateTo = {})
    }
}

@Preview("Home screen, open drawer dark theme")
@Composable
private fun PreviewDrawerOpenDark() {
    ThemedPreview(darkTheme = true) {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Open)
        )
        HomeScreen(
            postsRepository = BlockingFakePostsRepository(ContextAmbient.current),
            scaffoldState = scaffoldState,
            navigateTo = { }
        )
    }
}

@Composable
private fun loadFakePosts(): List<Post> {
    return previewDataFrom(BlockingFakePostsRepository(ContextAmbient.current)::getPosts)
}
