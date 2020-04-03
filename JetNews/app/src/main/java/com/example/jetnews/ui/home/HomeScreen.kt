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
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.material.Divider
import androidx.ui.material.DrawerState
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Scaffold
import androidx.ui.material.ScaffoldState
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.ripple
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.posts
import com.example.jetnews.model.Post
import com.example.jetnews.ui.AppDrawer
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.ThemedPreview
import com.example.jetnews.ui.darkThemeColors
import com.example.jetnews.ui.navigateTo

@Composable
fun HomeScreen(scaffoldState: ScaffoldState = remember { ScaffoldState() }) {
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
            HomeScreenBody(posts, modifier)
        }
    )
}

@Composable
private fun HomeScreenBody(
    posts: List<Post>,
    modifier: Modifier = Modifier.None
) {
    val postTop = posts[3]
    val postsSimple = posts.subList(0, 2)
    val postsPopular = posts.subList(2, 7)
    val postsHistory = posts.subList(7, 10)

    VerticalScroller {
        Column(modifier) {
            HomeScreenTopSection(postTop)
            HomeScreenSimpleSection(postsSimple)
            HomeScreenPopularSection(postsPopular)
            HomeScreenHistorySection(postsHistory)
        }
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
    Clickable(modifier = Modifier.ripple(), onClick = { navigateTo(Screen.Article(post.id)) }) {
        PostCardTop(post = post)
    }
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
        HorizontalScroller {
            Row(modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)) {
                posts.forEach { post ->
                    PostCardPopular(post, Modifier.padding(start = 16.dp))
                }
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
        modifier = Modifier.padding(start = 14.dp, end = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
}

@Preview("Home screen")
@Composable
fun PreviewHomeScreen() {
    ThemedPreview {
        HomeScreen()
    }
}

@Preview("Home screen, open drawer")
@Composable
private fun PreviewDrawerOpen() {
    ThemedPreview {
        HomeScreen(scaffoldState = ScaffoldState(drawerState = DrawerState.Opened))
    }
}

@Preview("Home screen dark theme")
@Composable
fun PreviewHomeScreenDark() {
    ThemedPreview(darkThemeColors) {
        HomeScreen()
    }
}

@Preview("Home screen, open drawer dark theme")
@Composable
private fun PreviewDrawerOpenDark() {
    ThemedPreview(darkThemeColors) {
        HomeScreen(scaffoldState = ScaffoldState(drawerState = DrawerState.Opened))
    }
}
