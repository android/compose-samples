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
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.FlexRow
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Padding
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.themeTextStyle
import androidx.ui.material.withOpacity
import androidx.ui.res.imageResource
import com.example.jetnews.R
import com.example.jetnews.model.Post
import com.example.jetnews.ui.JetnewsStatus
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.VectorImage
import com.example.jetnews.ui.navigateTo

@Composable
fun AuthorAndReadTime(post: Post) {
    FlexRow {
        val textStyle = (+themeTextStyle { body2 }).withOpacity(0.6f)
        flexible(1f) {
            Text(text = post.metadata.author.name, style = textStyle)
        }
        inflexible {
            Text(
                text = " - ${post.metadata.readTimeMinutes} min read",
                style = textStyle
            )
        }
    }
}

@Composable
fun PostImage(post: Post) {
    val image = post.imageThumb ?: +imageResource(R.drawable.placeholder_1_1)

    Container(width = 40.dp, height = 40.dp) {
        DrawImage(image)
    }
}

@Composable
fun PostTitle(post: Post) {
    Text(post.title, style = (+themeTextStyle { subtitle1 }).withOpacity(0.87f))
}

@Composable
fun PostCardSimple(post: Post) {
    Ripple(bounded = true) {
        Clickable(onClick = {
            navigateTo(Screen.Article(post.id))
        }) {
            Padding(16.dp) {
                FlexRow {
                    inflexible {
                        Padding(right = 16.dp) {
                            PostImage(post)
                        }
                    }
                    flexible(1f) {
                        Column(mainAxisSize = LayoutSize.Expand) {
                            PostTitle(post)
                            AuthorAndReadTime(post)
                        }
                    }
                    inflexible {
                        BookmarkButton(
                            isBookmarked = isFavorite(postId = post.id),
                            onBookmark = { toggleBookmark(postId = post.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostCardHistory(post: Post) {
    Ripple(bounded = true) {
        Clickable(onClick = {
            navigateTo(Screen.Article(post.id))
        }) {
            Padding(16.dp) {
                FlexRow {
                    inflexible {
                        Padding(right = 16.dp) {
                            PostImage(post = post)
                        }
                    }
                    flexible(1f) {
                        Column(mainAxisSize = LayoutSize.Expand) {
                            Text(
                                text = "BASED ON YOUR HISTORY",
                                style = (+themeTextStyle { overline }).withOpacity(0.38f)
                            )
                            PostTitle(post = post)
                            AuthorAndReadTime(post)
                        }
                    }
                    inflexible {
                        Padding(top = 8.dp, bottom = 8.dp) {
                            VectorImage(R.drawable.ic_more)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onBookmark: (Boolean) -> Unit
) {
    Ripple(
        bounded = false,
        radius = 24.dp
    ) {
        Toggleable(
            checked = isBookmarked,
            onCheckedChange = onBookmark
        ) {
            Container(width = 48.dp, height = 48.dp) {
                if (isBookmarked) {
                    VectorImage(R.drawable.ic_bookmarked)
                } else {
                    VectorImage(R.drawable.ic_bookmark)
                }
            }
        }
    }
}

fun toggleBookmark(postId: String) {
    with(JetnewsStatus) {
        if (favorites.contains(postId)) {
            favorites.remove(postId)
        } else {
            favorites.add(postId)
        }
    }
}

fun isFavorite(postId: String) = JetnewsStatus.favorites.contains(postId)
