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
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.LayoutSize
import androidx.ui.material.themeTextStyle
import androidx.ui.layout.HeightSpacer
import androidx.ui.material.surface.Card
import androidx.ui.layout.Padding
import androidx.ui.material.MaterialTheme
import com.android.tools.preview.Preview

@Composable
fun PostCardTop(post: Post) {
    // TUTORIAL CONTENT STARTS HERE
    Card {
        Padding(16.dp) {
            Column(
                mainAxisSize = LayoutSize.Wrap,
                crossAxisAlignment = CrossAxisAlignment.Start,
                crossAxisSize = LayoutSize.Expand
            ) {
                post.image?.let {
                    Card(shape = RoundedCornerShape(4.dp)) {
                        Container(expanded = true, height = 180.dp) {
                            ZoomedClippedImage(it)
                        }
                    }
                }
                HeightSpacer(16.dp)
                Opacity(0.87f) {
                    Text(post.title, +themeTextStyle { h6 })
                }
                Opacity(0.87f) {
                    Text(post.metadata.author.name, +themeTextStyle { body2 })
                }
                Opacity(0.6f) {
                    Text("${post.metadata.date} - ${post.metadata.readTimeMinutes} min read",
                        +themeTextStyle { body2 })
                }
            }
        }
    }
    // TUTORIAL CONTENT ENDS HERE
}

// Preview section

@Preview("Default colors")
@Composable
fun TutorialPreview() {
    val context = +ambient(ContextAmbient)
    val previewPosts = getPostsWithImagesLoaded(posts.subList(1, 2), context.resources)
    val post = previewPosts[0]
    // TODO: Cannot yet use typography in preview
    //  see b/141167679
    MaterialTheme(colors = lightThemeColors) {
        PostCardTop(post)
    }
}

@Preview("Dark colors")
@Composable
fun TutorialPreviewDark() {
    val context = +ambient(ContextAmbient)
    val previewPosts = getPostsWithImagesLoaded(posts.subList(1, 2), context.resources)
    val post = previewPosts[0]
    // TODO: Cannot yet use typography in preview
    //  see b/141167679
    MaterialTheme(colors = darkThemeColors) {
        PostCardTop(post)
    }
}

@Preview("Font scaling 1.5", fontScale = 1.5f)
@Composable
fun TutorialPreviewFontscale() {
    val context = +ambient(ContextAmbient)
    val previewPosts = getPostsWithImagesLoaded(posts.subList(1, 2), context.resources)
    val post = previewPosts[0]
    // TODO: Cannot yet use typography in preview
    //  see b/141167679
    MaterialTheme(colors = lightThemeColors) {
        PostCardTop(post)
    }
}
