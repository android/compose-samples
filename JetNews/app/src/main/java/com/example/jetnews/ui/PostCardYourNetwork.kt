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
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Padding
import androidx.ui.material.BaseButton
import androidx.ui.material.surface.Card
import androidx.ui.material.themeTextStyle
import androidx.ui.text.style.TextOverflow

@Composable
fun PostCardPopular(post: Post, icons: Icons) {
    Card(shape = RoundedCornerShape(4.dp)) {
        Container(width = 280.dp, height = 240.dp) {
            Column(
                mainAxisAlignment = MainAxisAlignment.Start,
                crossAxisAlignment = CrossAxisAlignment.Start,
                mainAxisSize = LayoutSize.Expand,
                crossAxisSize = LayoutSize.Expand
            ) {
                val image = post.image ?: icons.placeholder_1_1
                Container(width = 280.dp, height = 100.dp) {
                    ZoomedClippedImage(image)
                }
                Padding(16.dp) {
                    Column(crossAxisAlignment = CrossAxisAlignment.Start) {
                        BaseButton(onClick = {
                            navigateTo(Screen.Article(post.id))
                        }, color = Color.Transparent) {
                            Opacity(0.87f) {
                                Text(
                                    text = post.title,
                                    style = +themeTextStyle { h6 },
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Opacity(0.87f) {
                            Text(
                                text = post.metadata.author.name,
                                style = +themeTextStyle { body2 })
                        }
                        Opacity(0.6f) {
                            Text(text = "${post.metadata.date} - ${post.metadata.readTimeMinutes} min read",
                                style = +themeTextStyle { body2 })
                        }
                    }
                }
            }
        }
    }
}
