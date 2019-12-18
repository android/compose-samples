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
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutExpanded
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.material.EmphasisLevels
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import androidx.ui.res.imageResource
import androidx.ui.text.style.TextOverflow
import com.example.jetnews.R
import com.example.jetnews.model.Post
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.navigateTo

@Composable
fun PostCardPopular(post: Post) {
    Card(shape = RoundedCornerShape(4.dp)) {
        Ripple(bounded = true) {
            Clickable(onClick = {
                navigateTo(Screen.Article(post.id))
            }) {
                Container(modifier = LayoutSize(280.dp, 240.dp)) {
                    Column(modifier = LayoutExpanded) {
                        val image = post.image ?: imageResource(R.drawable.placeholder_4_3)
                        Container(modifier = LayoutHeight(100.dp) + LayoutExpanded) {
                            DrawImage(image)
                        }
                        Column(modifier = LayoutPadding(16.dp)) {

                            val emphasisLevels = EmphasisLevels()
                            ProvideEmphasis(emphasis = emphasisLevels.high) {
                                Text(
                                    text = post.title,
                                    style = MaterialTheme.typography().h6,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            ProvideEmphasis(emphasis = emphasisLevels.high) {
                                Text(
                                    text = post.metadata.author.name,
                                    style = MaterialTheme.typography().body2
                                )
                            }
                            ProvideEmphasis(emphasis = emphasisLevels.high) {
                                Text(
                                    text = "${post.metadata.date} - " +
                                            "${post.metadata.readTimeMinutes} min read",
                                    style = MaterialTheme.typography().body2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
