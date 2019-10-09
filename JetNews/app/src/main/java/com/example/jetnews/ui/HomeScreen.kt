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

package com.example.jetnews.ui

import androidx.compose.Composable
import androidx.compose.composer
import androidx.compose.unaryPlus
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.AppBarIcon
import androidx.ui.material.Divider
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.themeTextStyle
import androidx.ui.material.withOpacity

@Composable
fun HomeScreen(icons: Icons, openDrawer: () -> Unit) {
    val postTop = posts[1]
    val postsSimple = posts.subList(0, 2)
    val postsPopular = posts.subList(2, 7)
    val postsHistory = posts.subList(7, 10)

    val navigationIcon = @Composable {
        AppBarIcon(icons.menu) {
            openDrawer()
        }
    }

    FlexColumn {
        inflexible {
            TopAppBar(
                title = { Text(text = "Jetnews") },
                navigationIcon = navigationIcon
            )
        }
        flexible(flex = 1f) {
            VerticalScroller {
                Column(crossAxisAlignment = CrossAxisAlignment.Start) {
                    HomeScreenTopSection(post = postTop)
                    HomeScreenSimpleSection(posts = postsSimple, icons = icons)
                    HomeScreenPopularSection(posts = postsPopular, icons = icons)
                    HomeScreenHistorySection(posts = postsHistory, icons = icons)
                }
            }
        }
    }
}

@Composable
private fun HomeScreenTopSection(post: Post) {
    Padding(top = 16.dp, left = 16.dp, right = 16.dp) {
        Text(
            text = "Top stories for you",
            style = (+themeTextStyle { subtitle1 }).withOpacity(0.87f)
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
private fun HomeScreenSimpleSection(posts: List<Post>, icons: Icons) {
    posts.forEach { post ->
        PostCardSimple(post, icons)
        HomeScreenDivider()
    }
}

@Composable
private fun HomeScreenPopularSection(posts: List<Post>, icons: Icons) {
    Padding(16.dp) {
        Text(
            text = "Popular on Jetnews",
            style = (+themeTextStyle { subtitle1 }).withOpacity(0.87f)
        )
    }
    HorizontalScroller {
        Row(
            mainAxisAlignment = MainAxisAlignment.Start,
            crossAxisAlignment = CrossAxisAlignment.Start
        ) {
            posts.forEach { post ->
                WidthSpacer(16.dp)
                PostCardPopular(post, icons)
            }
        }
    }
    HeightSpacer(16.dp)
    HomeScreenDivider()
}

@Composable
private fun HomeScreenHistorySection(posts: List<Post>, icons: Icons) {
    posts.forEach { post ->
        PostCardHistory(post, icons)
        HomeScreenDivider()
    }
}

@Composable
private fun HomeScreenDivider() {
    Padding(left = 14.dp, right = 14.dp) {
        Opacity(0.08f) {
            Divider()
        }
    }
}
