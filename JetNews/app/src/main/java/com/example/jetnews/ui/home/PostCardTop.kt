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
import androidx.ui.core.Clip
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Text
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.surface.Surface
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.data.getPostsWithImagesLoaded
import com.example.jetnews.data.post2
import com.example.jetnews.data.posts
import com.example.jetnews.model.Post
import com.example.jetnews.ui.darkThemeColors
import com.example.jetnews.ui.lightThemeColors
import com.example.jetnews.ui.themeTypography

@Composable
fun PostCardTop(post: Post) {
// TUTORIAL CONTENT STARTS HERE
    val typography = MaterialTheme.typography()
    Column(modifier = LayoutWidth.Fill + LayoutPadding(16.dp)) {
        post.image?.let { image ->
            Container(modifier = LayoutHeight.Min(180.dp) + LayoutWidth.Fill) {
                Clip(shape = RoundedCornerShape(4.dp)) {
                    SimpleImage(image = image)
                }
            }
        }
        Spacer(LayoutHeight(16.dp))

        val emphasisLevels = EmphasisLevels()
        ProvideEmphasis(emphasis = emphasisLevels.high) {
            Text(
                text = post.title,
                style = typography.h6
            )
        }
        ProvideEmphasis(emphasis = emphasisLevels.high) {
            Text(
                text = post.metadata.author.name,
                style = typography.body2
            )
        }
        ProvideEmphasis(emphasis = emphasisLevels.medium) {
            Text(
                text = "${post.metadata.date} - ${post.metadata.readTimeMinutes} min read",
                style = typography.body2
            )
        }
    }
}
// TUTORIAL CONTENT ENDS HERE

// Preview section

@Preview("Default colors")
@Composable
fun TutorialPreview() {
    TutorialPreviewTemplate()
}

@Preview("Dark colors")
@Composable
fun TutorialPreviewDark() {
    TutorialPreviewTemplate(colors = darkThemeColors)
}

@Preview("Font scaling 1.5", fontScale = 1.5f)
@Composable
fun TutorialPreviewFontscale() {
    TutorialPreviewTemplate()
}

@Composable
fun TutorialPreviewTemplate(
    colors: ColorPalette = lightThemeColors,
    typography: Typography = themeTypography
) {
    val context = ContextAmbient.current
    val previewPosts = getPostsWithImagesLoaded(posts.subList(1, 2), context.resources)
    val post = previewPosts[0]
    MaterialTheme(colors = colors, typography = typography) {
        Surface {
            PostCardTop(post)
        }
    }
}

@Preview
@Composable
fun previewPostCardTop() {
    PostCardTop(post = post2)
}
