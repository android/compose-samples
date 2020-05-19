/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.article

import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.foundation.Box
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.material.ColorPalette
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Surface
import androidx.ui.material.Typography
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.AccountCircle
import androidx.ui.text.AnnotatedString
import androidx.ui.text.FirstBaseline
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.SpanStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextDecoration
import androidx.ui.text.style.TextIndent
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.model.Markup
import com.example.jetnews.model.MarkupType
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Paragraph
import com.example.jetnews.model.ParagraphType
import com.example.jetnews.model.Post
import com.example.jetnews.ui.ThemedPreview

private val defaultSpacerSize = 16.dp

@Composable
fun PostContent(post: Post, modifier: Modifier = Modifier) {
    VerticalScroller {
        Column(modifier = modifier.padding(start = defaultSpacerSize, end = defaultSpacerSize)) {
            Spacer(Modifier.preferredHeight(defaultSpacerSize))
            PostHeaderImage(post)
            Text(text = post.title, style = MaterialTheme.typography.h4)
            Spacer(Modifier.preferredHeight(8.dp))
            post.subtitle?.let { subtitle ->
                ProvideEmphasis(EmphasisAmbient.current.medium) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.body2.merge(TextStyle(lineHeight = 20.sp))
                    )
                }
                Spacer(Modifier.preferredHeight(defaultSpacerSize))
            }
            PostMetadata(post.metadata)
            Spacer(Modifier.preferredHeight(24.dp))
            PostContents(post.paragraphs)
            Spacer(Modifier.preferredHeight(48.dp))
        }
    }
}

@Composable
private fun PostHeaderImage(post: Post) {
    post.image?.let { image ->
        val imageModifier = Modifier
            .preferredHeightIn(minHeight = 180.dp)
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
        Image(image, imageModifier, contentScale = ContentScale.Crop)
        Spacer(Modifier.preferredHeight(defaultSpacerSize))
    }
}

@Composable
private fun PostMetadata(metadata: Metadata) {
    val typography = MaterialTheme.typography
    Row {
        Image(
            asset = Icons.Filled.AccountCircle,
            modifier = Modifier.preferredSize(40.dp),
            colorFilter = ColorFilter.tint(contentColor()),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.preferredWidth(8.dp))
        Column {
            ProvideEmphasis(EmphasisAmbient.current.high) {
                Text(
                    text = metadata.author.name,
                    style = typography.caption,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            ProvideEmphasis(EmphasisAmbient.current.medium) {
                Text(
                    text = "${metadata.date} • ${metadata.readTimeMinutes} min read",
                    style = typography.caption
                )
            }
        }
    }
}

@Composable
private fun PostContents(paragraphs: List<Paragraph>) {
    paragraphs.forEach {
        Paragraph(paragraph = it)
    }
}

@Composable
private fun Paragraph(paragraph: Paragraph) {
    val (textStyle, paragraphStyle, trailingPadding) = paragraph.type.getTextAndParagraphStyle()

    val annotatedString = paragraphToAnnotatedString(
        paragraph,
        MaterialTheme.typography,
        MaterialTheme.colors.codeBlockBackground
    )
    Box(modifier = Modifier.padding(bottom = trailingPadding)) {
        when (paragraph.type) {
            ParagraphType.Bullet -> BulletParagraph(
                text = annotatedString,
                textStyle = textStyle,
                paragraphStyle = paragraphStyle
            )
            ParagraphType.CodeBlock -> CodeBlockParagraph(
                text = annotatedString,
                textStyle = textStyle,
                paragraphStyle = paragraphStyle
            )
            ParagraphType.Header -> {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = annotatedString,
                    style = textStyle.merge(paragraphStyle)
                )
            }
            else -> Text(
                modifier = Modifier.padding(4.dp),
                text = annotatedString,
                style = textStyle
            )
        }
    }
}

@Composable
private fun CodeBlockParagraph(
    text: AnnotatedString,
    textStyle: TextStyle,
    paragraphStyle: ParagraphStyle
) {
    Surface(
        color = MaterialTheme.colors.codeBlockBackground,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }
}

@Composable
private fun BulletParagraph(
    text: AnnotatedString,
    textStyle: TextStyle,
    paragraphStyle: ParagraphStyle
) {
    Row {
        with(DensityAmbient.current) {
            // this box is acting as a character, so it's sized with font scaling (sp)
            Box(
                modifier = Modifier
                    .preferredSize(8.sp.toDp(), 8.sp.toDp())
                    .alignWithSiblings {
                        // Add an alignment "baseline" 1sp below the bottom of the circle
                        9.sp.toIntPx()
                    },
                backgroundColor = contentColor(),
                shape = CircleShape
            )
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .alignWithSiblings(FirstBaseline),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }
}

private data class ParagraphStyling(
    val textStyle: TextStyle,
    val paragraphStyle: ParagraphStyle,
    val trailingPadding: Dp
)

@Composable
private fun ParagraphType.getTextAndParagraphStyle(): ParagraphStyling {
    val typography = MaterialTheme.typography
    var textStyle: TextStyle = typography.body1
    var paragraphStyle = ParagraphStyle()
    var trailingPadding = 24.dp

    when (this) {
        ParagraphType.Caption -> textStyle = typography.body1
        ParagraphType.Title -> textStyle = typography.h4
        ParagraphType.Subhead -> {
            textStyle = typography.h6
            trailingPadding = 16.dp
        }
        ParagraphType.Text -> {
            textStyle = typography.body1
            paragraphStyle = paragraphStyle.copy(lineHeight = 28.sp)
        }
        ParagraphType.Header -> {
            textStyle = typography.h5
            trailingPadding = 16.dp
        }
        ParagraphType.CodeBlock -> textStyle = typography.body1.copy(
            fontFamily = FontFamily.Monospace
        )
        ParagraphType.Quote -> textStyle = typography.body1
        ParagraphType.Bullet -> {
            paragraphStyle = ParagraphStyle(textIndent = TextIndent(firstLine = 8.sp))
        }
    }
    return ParagraphStyling(
        textStyle,
        paragraphStyle,
        trailingPadding
    )
}

private fun paragraphToAnnotatedString(
    paragraph: Paragraph,
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString {
    val styles: List<AnnotatedString.Item<SpanStyle>> = paragraph.markups
        .map { it.toAnnotatedStringItem(typography, codeBlockBackground) }
    return AnnotatedString(text = paragraph.text, spanStyles = styles)
}

fun Markup.toAnnotatedStringItem(
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString.Item<SpanStyle> {
    return when (this.type) {
        MarkupType.Italic -> {
            AnnotatedString.Item(
                typography.body1.copy(fontStyle = FontStyle.Italic).toSpanStyle(),
                start,
                end
            )
        }
        MarkupType.Link -> {
            AnnotatedString.Item(
                typography.body1.copy(textDecoration = TextDecoration.Underline).toSpanStyle(),
                start,
                end
            )
        }
        MarkupType.Bold -> {
            AnnotatedString.Item(
                typography.body1.copy(fontWeight = FontWeight.Bold).toSpanStyle(),
                start,
                end
            )
        }
        MarkupType.Code -> {
            AnnotatedString.Item(
                typography.body1
                    .copy(
                        background = codeBlockBackground,
                        fontFamily = FontFamily.Monospace
                    ).toSpanStyle(),
                start,
                end
            )
        }
    }
}

private val ColorPalette.codeBlockBackground: Color
    get() = onSurface.copy(alpha = .15f)

@Preview("Post content")
@Composable
fun PreviewPost() {
    ThemedPreview {
        PostContent(post = post3)
    }
}

@Preview("Post content dark theme")
@Composable
fun PreviewPostDark() {
    ThemedPreview(darkTheme = true) {
        PostContent(post = post3)
    }
}
