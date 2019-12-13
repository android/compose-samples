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

package com.example.jetnews.ui.article

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Clip
import androidx.ui.core.Dp
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.sp
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.ExpandedWidth
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MinHeight
import androidx.ui.layout.Row
import androidx.ui.layout.Size
import androidx.ui.layout.Spacing
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import androidx.ui.material.withOpacity
import androidx.ui.text.AnnotatedString
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextDecoration
import androidx.ui.text.style.TextIndent
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.post3
import com.example.jetnews.model.Markup
import com.example.jetnews.model.MarkupType
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Paragraph
import com.example.jetnews.model.ParagraphType
import com.example.jetnews.model.Post
import com.example.jetnews.ui.VectorImage

private val defaultSpacerSize = 16.dp
private val codeBlockBackground = Color(0xfff1f1f1.toInt())

@Composable
fun PostContent(modifier: Modifier = Modifier.None, post: Post) {
    val typography = +MaterialTheme.typography()
    VerticalScroller(modifier = modifier) {
        Column(modifier = Spacing(left = defaultSpacerSize, right = defaultSpacerSize)) {
            HeightSpacer(height = defaultSpacerSize)
            PostHeaderImage(post)
            Text(text = post.title, style = typography.h4)
            HeightSpacer(height = 8.dp)
            post.subtitle?.let { subtitle ->
                Text(
                    text = subtitle,
                    style = typography.body2.withOpacity(0.6f),
                    paragraphStyle = ParagraphStyle(lineHeight = 20.sp)
                )
                HeightSpacer(height = defaultSpacerSize)
            }
            PostMetadata(metadata = post.metadata)
            HeightSpacer(height = 24.dp)
            PostContents(paragraphs = post.paragraphs)
            HeightSpacer(height = 48.dp)
        }
    }
}

@Composable
private fun PostHeaderImage(post: Post) {
    post.image?.let { image ->
        Container(modifier = MinHeight(180.dp) wraps ExpandedWidth) {
            Clip(shape = RoundedCornerShape(4.dp)) {
                DrawImage(image)
            }
        }
        HeightSpacer(height = defaultSpacerSize)
    }
}

@Composable
private fun PostMetadata(metadata: Metadata) {
    val typography = +MaterialTheme.typography()
    Row {
        VectorImage(id = R.drawable.ic_account_circle_black)
        WidthSpacer(width = 8.dp)
        Column {
            HeightSpacer(4.dp)
            Text(
                text = metadata.author.name,
                style = typography.caption.withOpacity(0.87f)
            )
            Text(
                text = "${metadata.date} • ${metadata.readTimeMinutes} min read",
                style = typography.caption.withOpacity(0.6f)
            )
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

    val annotatedString = paragraphToAnnotatedString(paragraph)
    Container(modifier = Spacing(bottom = trailingPadding)) {
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
                    modifier = Spacing(top = 16.dp),
                    text = annotatedString,
                    style = textStyle,
                    paragraphStyle = paragraphStyle
                )
            }
            else -> Text(
                text = annotatedString,
                style = textStyle,
                paragraphStyle = paragraphStyle
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
        color = codeBlockBackground,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            modifier = Spacing(top = 16.dp),
            text = text,
            style = textStyle,
            paragraphStyle = paragraphStyle
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
        Container(modifier = Size(8.dp, 8.dp)) {
            DrawShape(shape = CircleShape, color = Color.DarkGray)
        }
        Text(
            modifier = Flexible(1f),
            text = text,
            style = textStyle,
            paragraphStyle = paragraphStyle
        )
    }
}

private data class ParagraphStyling(
    val textStyle: TextStyle,
    val paragraphStyle: ParagraphStyle,
    val trailingPadding: Dp
)

private fun ParagraphType.getTextAndParagraphStyle(): ParagraphStyling {
    val typography = +MaterialTheme.typography()
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
        ParagraphType.CodeBlock -> textStyle =
            (typography.body1)
                .copy(background = codeBlockBackground, fontFamily = FontFamily.Monospace)
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

private fun paragraphToAnnotatedString(paragraph: Paragraph): AnnotatedString {
    val styles = paragraph.markups.map { it.toAnnotatedStringItem() }
    return AnnotatedString(text = paragraph.text, textStyles = styles)
}

private fun Markup.toAnnotatedStringItem(): AnnotatedString.Item<TextStyle> {
    val typography = +MaterialTheme.typography()
    return when (this.type) {
        MarkupType.Italic -> {
            AnnotatedString.Item(
                typography.body1.copy(fontStyle = FontStyle.Italic),
                start,
                end
            )
        }
        MarkupType.Link -> {
            AnnotatedString.Item(
                typography.body1.copy(decoration = TextDecoration.Underline),
                start,
                end
            )
        }
        MarkupType.Bold -> {
            AnnotatedString.Item(
                typography.body1.copy(fontWeight = FontWeight.Bold),
                start,
                end
            )
        }
        MarkupType.Code -> {
            AnnotatedString.Item(
                typography.body1
                    .copy(background = codeBlockBackground, fontFamily = FontFamily.Monospace),
                start,
                end
            )
        }
    }
}

@Preview
@Composable
fun preview() {
    PostContent(post = post3)
}
