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
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Clip
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.ExpandedWidth
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MinHeight
import androidx.ui.layout.Spacing
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Typography
import androidx.ui.material.surface.Surface
import androidx.ui.material.withOpacity
import androidx.ui.tooling.preview.Preview
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
    val typography = +MaterialTheme.typography()
    Column(modifier = ExpandedWidth wraps Spacing(16.dp)) {
        post.image?.let { image ->
            Container(modifier = MinHeight(180.dp) wraps ExpandedWidth) {
                Clip(shape = RoundedCornerShape(4.dp)) {
                    DrawImage(image)
                }
            }
        }
        HeightSpacer(16.dp)
        Text(
            text = post.title,
            style = typography.h6.withOpacity(0.87f)
        )
        Text(
            text = post.metadata.author.name,
            style = typography.body2.withOpacity(0.87f)
        )
        Text(
            text = "${post.metadata.date} - ${post.metadata.readTimeMinutes} min read",
            style = typography.body2.withOpacity(0.6f)
        )
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
    val context = +ambient(ContextAmbient)
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
