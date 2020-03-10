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
import androidx.ui.core.toModifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.ScaleFit
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Spacer
import androidx.ui.material.ColorPalette
import androidx.ui.material.EmphasisLevels
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Typography
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
            // FIXME (dev07): This duplicate sizing is a workaround for the way Draw modifiers work in
            // dev06. In a future release (dev07?) replace this with a single Image composable with a
            // Clip modifier applied.

            // This sizing is currently applied twice to make the image expand propertly inside the
            // Clip.
            val sizeModifier = LayoutHeight.Min(180.dp) + LayoutWidth.Fill
            Container(modifier = sizeModifier) {
                Clip(shape = RoundedCornerShape(4.dp)) {
                    val imageModifier = ImagePainter(image).toModifier(scaleFit = ScaleFit.FillHeight)
                    Box(modifier = sizeModifier + imageModifier)
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
