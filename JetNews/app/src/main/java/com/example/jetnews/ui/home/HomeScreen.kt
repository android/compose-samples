/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.home

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.Row
import androidx.ui.material.Divider
import androidx.ui.material.DrawerState
import androidx.ui.material.EmphasisLevels
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Scaffold
import androidx.ui.material.ScaffoldState
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts
import com.example.jetnews.model.Post
import com.example.jetnews.ui.AppDrawer
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.VectorImageButton
import com.example.jetnews.ui.navigateTo

@Composable
fun HomeScreen(scaffoldState: ScaffoldState = remember { ScaffoldState() }) {
    Column {
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
                        VectorImageButton(R.drawable.ic_jetnews_logo) {
                            scaffoldState.drawerState = DrawerState.Opened
                        }
                    }
                )
            },
            bodyContent = { modifier ->
                HomeScreenBody(modifier = modifier, posts = posts)
            }
        )
    }
}

@Composable
private fun HomeScreenBody(
    modifier: Modifier = Modifier.None,
    posts: List<Post>
) {
    val postTop = posts[3]
    val postsSimple = posts.subList(0, 2)
    val postsPopular = posts.subList(2, 7)
    val postsHistory = posts.subList(7, 10)

    VerticalScroller {
        Column(modifier = modifier) {
            HomeScreenTopSection(post = postTop)
            HomeScreenSimpleSection(posts = postsSimple)
            HomeScreenPopularSection(posts = postsPopular)
            HomeScreenHistorySection(posts = postsHistory)
        }
    }
}

@Composable
private fun HomeScreenTopSection(post: Post) {
    ProvideEmphasis(emphasis = EmphasisLevels().high) {
        Text(
            modifier = LayoutPadding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
            text = "Top stories for you",
            style = MaterialTheme.typography().subtitle1
        )
    }
    Ripple(bounded = true) {
        Clickable(onClick = {
            navigateTo(Screen.Article(post.id))
        }) {
            PostCardTop(post = post)
        }
    }
    HomeScreenDivider()
}

@Composable
private fun HomeScreenSimpleSection(posts: List<Post>) {
    posts.forEach { post ->
        PostCardSimple(post)
        HomeScreenDivider()
    }
}

@Composable
private fun HomeScreenPopularSection(posts: List<Post>) {
    ProvideEmphasis(emphasis = EmphasisLevels().high) {
        Text(
            modifier = LayoutPadding(16.dp),
            text = "Popular on Jetnews",
            style = MaterialTheme.typography().subtitle1
        )
    }
    HorizontalScroller {
        Row(modifier = LayoutPadding(top = 0.dp, start = 0.dp, end = 16.dp, bottom = 16.dp)) {
            posts.forEach { post ->
                PostCardPopular(modifier = LayoutPadding(start = 16.dp), post = post)
            }
        }
    }
    HomeScreenDivider()
}

@Composable
private fun HomeScreenHistorySection(posts: List<Post>) {
    posts.forEach { post ->
        PostCardHistory(post)
        HomeScreenDivider()
    }
}

@Composable
private fun HomeScreenDivider() {
    Opacity(0.08f) {
        Divider(modifier = LayoutPadding(start = 14.dp, top = 0.dp, end = 14.dp, bottom = 0.dp))
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}

@Preview
@Composable
private fun PreviewDrawerOpen() {
    HomeScreen(scaffoldState = ScaffoldState(drawerState = DrawerState.Opened))
}
