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

import androidx.compose.composer

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.foundation.selection.ToggleableState
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.FlexRow
import androidx.ui.layout.FlexSize
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.layout.Wrap
import androidx.ui.material.BaseButton
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import androidx.ui.material.themeTextStyle
import androidx.ui.material.AppBarIcon
import androidx.ui.material.Divider
import androidx.ui.material.TopAppBar
import androidx.ui.material.surface.Surface

@Composable
fun AuthorAndReadTime(post: Post) {
    FlexRow {
        flexible(1f) {
            Text(post.metadata.author.name)
        }
        inflexible {
            Text(" - ${post.metadata.readTimeMinutes} min read")
        }
    }
}

@Composable
fun PostImage(post: Post, icons: Icons) {
    Clickable(onClick = {
        navigateTo(destination = Screen.Article(post.id))
    }) {
        Container(width = 40.dp, height = 40.dp) {
            FittedImage(image = icons.placeholder_1_1)
        }
    }
}

@Composable
fun PostTitle(post: Post) {
    BaseButton(onClick = {
        navigateTo(destination = Screen.Article(post.id))
    }, color = Color.Transparent) {
        Text(post.title, style = +themeTextStyle { subtitle1 })
    }
}

@Composable
fun PostCardTop(post: Post, icons: Icons) {
    Padding(16.dp) {
        FlexRow {
            inflexible {
                Padding(right = 16.dp) {
                    PostImage(post, icons)
                }
            }
            flexible(1f) {
                Column(
                    crossAxisAlignment = CrossAxisAlignment.Start,
                    crossAxisSize = FlexSize.Wrap,
                    mainAxisAlignment = MainAxisAlignment.Start,
                    mainAxisSize = FlexSize.Expand
                ) {
                    PostTitle(post)
                    AuthorAndReadTime(post)
                }
            }
            inflexible {
                Padding(top = 8.dp, bottom = 8.dp) {
                    BookmarkButton(post, icons)
                }
            }
        }
    }
}

@Composable
fun PostCardHistory(post: Post, icons: Icons) {
    Padding(16.dp) {
        FlexRow {
            inflexible {
                Padding(right = 16.dp) {
                    PostImage(post = post, icons = icons)
                }
            }
            flexible(1f) {
                Column(
                    crossAxisAlignment = CrossAxisAlignment.Start,
                    crossAxisSize = FlexSize.Wrap,
                    mainAxisAlignment = MainAxisAlignment.Start,
                    mainAxisSize = FlexSize.Expand
                ) {
                    Text("Based on your history")
                    PostTitle(post = post)
                    AuthorAndReadTime(post)
                }
            }
            inflexible {
                Padding(top = 8.dp, bottom = 8.dp) {
                    SimpleImage(icons.more)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(icons: Icons, openDrawer: () -> Unit) {
    val postsTop = posts.subList(0, 2)
    val postsNetwork = posts.subList(2, 7)
    val postsHistory = posts.subList(7, 10)

    val homeScreenTop = @Composable {
        postsTop.forEach { post ->
            PostCardTop(post, icons)
            Divider()
        }
    }

    val homeScreenNetwork = @Composable {
        Padding(16.dp) {
            Text("New from your network")
        }
        Row(
            mainAxisAlignment = MainAxisAlignment.Start,
            crossAxisAlignment = CrossAxisAlignment.Start
        ) {
            postsNetwork.forEach { post ->
                WidthSpacer(16.dp)
                PostCardYourNetwork(post, icons)
            }
        }
        HeightSpacer(16.dp)
        Divider()
    }

    val homeScreenHistory = @Composable {
        postsHistory.forEach { post ->
            PostCardHistory(post, icons)
            Divider()
        }
    }

    val navigationIcon = @Composable {
        AppBarIcon(icons.menu) {
            openDrawer()
        }
    }

    FlexColumn {
        inflexible {
            TopAppBar<Any>(
                { Text("JetNews") },
                navigationIcon = navigationIcon
            )
        }
        flexible(flex = 1f) {
            Column(crossAxisAlignment = CrossAxisAlignment.Start) {
                homeScreenTop()
                homeScreenNetwork()
                homeScreenHistory()
            }
        }
    }
}

@Composable
fun BookmarkButton(post: Post, icons: Icons) {
    val bookmarked = jetNewsStatus.favorites.contains(post.id)
    val value = if (bookmarked) ToggleableState.Checked else ToggleableState.Unchecked
    Surface {
        Ripple(bounded = true) {
            Toggleable(
                value = value,
                onToggle = { toggleBookmark(post.id) }) {
                when (value) {
                    ToggleableState.Checked -> SimpleImage(icons.bookmarkOn)
                    else -> SimpleImage(icons.bookmarkOff)
                }
            }
        }
    }
}