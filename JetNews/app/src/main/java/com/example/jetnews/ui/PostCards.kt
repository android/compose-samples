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
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.foundation.selection.ToggleableState
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.FlexRow
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Padding
import androidx.ui.material.BaseButton
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeColor
import androidx.ui.material.themeTextStyle

@Composable
fun AuthorAndReadTime(post: Post) {
    Opacity(0.6f) {
        FlexRow {
            flexible(1f) {
                Text(text = post.metadata.author.name, style = +themeTextStyle { body2 })
            }
            inflexible {
                Text(
                    text = " - ${post.metadata.readTimeMinutes} min read",
                    style = +themeTextStyle { body2 })
            }
        }
    }
}

@Composable
fun PostImage(post: Post, icons: Icons) {
    val image = post.image ?: icons.placeholder_1_1

    Container(width = 40.dp, height = 40.dp) {
        ZoomedClippedImage(image)
    }
}

@Composable
fun PostTitle(post: Post) {
    Opacity(0.87f) {
        Text(post.title, style = +themeTextStyle { subtitle1 })
    }
}

@Composable
fun PostCardSimple(post: Post, icons: Icons) {
    BaseButton(
        onClick = {
            navigateTo(Screen.Article(post.id))
        }, color = +themeColor { surface }
    ) {
        Padding(16.dp) {
            FlexRow(crossAxisAlignment = CrossAxisAlignment.Start) {
                inflexible {
                    Padding(right = 16.dp) {
                        PostImage(post, icons)
                    }
                }
                flexible(1f) {
                    Column(
                        crossAxisAlignment = CrossAxisAlignment.Start,
                        crossAxisSize = LayoutSize.Wrap,
                        mainAxisAlignment = MainAxisAlignment.Start,
                        mainAxisSize = LayoutSize.Expand
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
}

@Composable
fun PostCardHistory(post: Post, icons: Icons) {
    BaseButton(
        onClick = {
            navigateTo(Screen.Article(post.id))
        },
        color = +themeColor { surface }
    ) {
        Padding(16.dp) {
            FlexRow(crossAxisAlignment = CrossAxisAlignment.Start) {
                inflexible {
                    Padding(right = 16.dp) {
                        PostImage(post = post, icons = icons)
                    }
                }
                flexible(1f) {
                    Column(
                        crossAxisAlignment = CrossAxisAlignment.Start,
                        crossAxisSize = LayoutSize.Wrap,
                        mainAxisAlignment = MainAxisAlignment.Start,
                        mainAxisSize = LayoutSize.Expand
                    ) {
                        Opacity(0.38f) {
                            Text(
                                text = "BASED ON YOUR HISTORY",
                                style = +themeTextStyle { overline })
                        }
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
}

@Composable
fun BookmarkButton(post: Post, icons: Icons) {
    val value = getToggleableState(post.id)
    Surface {
        Ripple(bounded = true) {
            Toggleable(
                value = value,
                onToggle = { toggleBookmark(post.id) }
            ) {
                when (value) {
                    ToggleableState.Checked -> SimpleImage(icons.bookmarkOn)
                    else -> SimpleImage(icons.bookmarkOff)
                }
            }
        }
    }
}

fun toggleBookmark(postId: String) {
    JetnewsStatus.run {
        if (favorites.contains(postId)) {
            favorites.remove(postId)
        } else {
            favorites.add(postId)
        }
    }
}

fun getToggleableState(postId: String): ToggleableState {
    val bookmarked = JetnewsStatus.favorites.contains(postId)
    return if (bookmarked) ToggleableState.Checked else ToggleableState.Unchecked
}
