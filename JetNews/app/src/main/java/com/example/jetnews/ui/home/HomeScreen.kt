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
import androidx.compose.unaryPlus
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacing
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.withOpacity
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.posts
import com.example.jetnews.model.Post
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.VectorImageButton
import com.example.jetnews.ui.navigateTo

@Composable
fun HomeScreen(openDrawer: () -> Unit) {
    val postTop = posts[3]
    val postsSimple = posts.subList(0, 2)
    val postsPopular = posts.subList(2, 7)
    val postsHistory = posts.subList(7, 10)

    Column {
        TopAppBar(
            title = { Text(text = "Jetnews") },
            navigationIcon = {
                VectorImageButton(R.drawable.ic_jetnews_logo) {
                    openDrawer()
                }
            }
        )
        VerticalScroller(modifier = Flexible(1f)) {
            Column {
                HomeScreenTopSection(post = postTop)
                HomeScreenSimpleSection(posts = postsSimple)
                HomeScreenPopularSection(posts = postsPopular)
                HomeScreenHistorySection(posts = postsHistory)
            }
        }
    }
}

@Composable
private fun HomeScreenTopSection(post: Post) {

    Text(
        modifier = Spacing(top = 16.dp, left = 16.dp, right = 16.dp),
        text = "Top stories for you",
        style = ((+MaterialTheme.typography()).subtitle1).withOpacity(0.87f)
    )
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
    Text(
        modifier = Spacing(16.dp),
        text = "Popular on Jetnews",
        style = ((+MaterialTheme.typography()).subtitle1).withOpacity(0.87f)
    )
    HorizontalScroller {
        Row(modifier = Spacing(bottom = 16.dp, right = 16.dp)) {
            posts.forEach { post ->
                WidthSpacer(16.dp)
                PostCardPopular(post)
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
        Divider(modifier = Spacing(left = 14.dp, right = 14.dp))
    }
}

@Preview
@Composable
fun preview() {
    HomeScreen {}
}
