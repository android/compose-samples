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
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.toModifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import androidx.ui.res.imageResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.post1
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostAuthor
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.navigateTo

private val cardSize = LayoutSize(280.dp, 240.dp)

@Composable
fun PostCardPopular(modifier: Modifier = Modifier.None, post: Post) {
    Card(modifier = modifier, shape = RoundedCornerShape(4.dp)) {
        Ripple(bounded = true) {
            Clickable(onClick = {
                navigateTo(Screen.Article(post.id))
            }) {
                Column(modifier = cardSize) {
                    val image = post.image ?: imageResource(R.drawable.placeholder_4_3)
                    val imageModifier = ImagePainter(image).toModifier()
                    Box(modifier = LayoutHeight(100.dp) + LayoutSize.Fill + imageModifier)
                    Column(modifier = LayoutPadding(16.dp)) {
                        val emphasisLevels = MaterialTheme.emphasisLevels()
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
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
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

@Preview
@Composable
fun PreviewPostCardPopular() {
    PostCardPopular(post = post1)
}

@Preview
@Composable
fun PreviewPostCardPopularLongText() {

    val loremIpsum = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras ullamcorper pharetra massa,
        sed suscipit nunc mollis in. Sed tincidunt orci lacus, vel ullamcorper nibh congue quis.
        Etiam imperdiet facilisis ligula id facilisis. Suspendisse potenti. Cras vehicula neque sed
        nulla auctor scelerisque. Vestibulum at congue risus, vel aliquet eros. In arcu mauris,
        facilisis eget magna quis, rhoncus volutpat mi. Phasellus vel sollicitudin quam, eu
        consectetur dolor. Proin lobortis venenatis sem, in vestibulum est. Duis ac nibh interdum,
    """.trimIndent()
    val post = post1
    PostCardPopular(post = post.copy(
        title = "Title" + loremIpsum,
        metadata = post.metadata.copy(
            author = PostAuthor("Author: " + loremIpsum),
            readTimeMinutes = Int.MAX_VALUE
        )
    ))
}
