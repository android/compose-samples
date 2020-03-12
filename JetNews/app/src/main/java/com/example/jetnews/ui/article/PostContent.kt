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
import androidx.ui.core.Clip
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.toModifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.ScaleFit
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.material.EmphasisLevels
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Typography
import androidx.ui.material.surface.Surface
import androidx.ui.text.AnnotatedString
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
    val typography = MaterialTheme.typography()
    VerticalScroller {
        Column(
            modifier = modifier + LayoutPadding(
                start = defaultSpacerSize,
                top = 0.dp,
                end = defaultSpacerSize,
                bottom = 0.dp
            )
        ) {
            Spacer(LayoutHeight(defaultSpacerSize))
            PostHeaderImage(post)
            Text(text = post.title, style = typography.h4)
            Spacer(LayoutHeight(8.dp))
            post.subtitle?.let { subtitle ->
                ProvideEmphasis(emphasis = EmphasisLevels().medium) {
                    Text(
                        text = subtitle,
                        style = typography.body2.merge(TextStyle(lineHeight = 20.sp))
                    )
                }
                Spacer(LayoutHeight(defaultSpacerSize))
            }
            PostMetadata(metadata = post.metadata)
            Spacer(LayoutHeight(24.dp))
            PostContents(paragraphs = post.paragraphs)
            Spacer(LayoutHeight(48.dp))
        }
    }
}

@Composable
private fun PostHeaderImage(post: Post) {
    post.image?.let { image ->
        // FIXME (dev07): This duplicate sizing is a workaround for the way Draw modifiers work in
        // dev06. In a future release (dev07?) replace this with a single Image composable with a
        // Clip modifier applied.

        // This sizing is currently applied twice to make the image expand propertly inside the
        // Clip.
        val sizingModifier = LayoutHeight.Min(180.dp) + LayoutWidth.Fill
        Box(modifier = sizingModifier) {
            Clip(shape = RoundedCornerShape(4.dp)) {
                val imageModifier = ImagePainter(image).toModifier(scaleFit = ScaleFit.FillHeight)
                Box(modifier = sizingModifier + imageModifier)
            }
        }
        Spacer(LayoutHeight(defaultSpacerSize))
    }
}

@Composable
private fun PostMetadata(metadata: Metadata) {
    val typography = MaterialTheme.typography()
    Row {
        VectorImage(id = R.drawable.ic_account_circle_black)
        Spacer(LayoutWidth(8.dp))
        Column {
            Spacer(LayoutHeight(4.dp))
            ProvideEmphasis(emphasis = EmphasisLevels().high) {
                Text(
                    text = metadata.author.name,
                    style = typography.caption
                )
            }
            ProvideEmphasis(emphasis = EmphasisLevels().medium) {
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

    val annotatedString = paragraphToAnnotatedString(paragraph, MaterialTheme.typography())
    Container(modifier = LayoutPadding(0.dp, 0.dp, 0.dp, bottom = trailingPadding)) {
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
                    modifier = LayoutPadding(4.dp),
                    text = annotatedString,
                    style = textStyle.merge(paragraphStyle)
                )
            }
            else -> Text(
                modifier = LayoutPadding(4.dp),
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
        color = codeBlockBackground,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            modifier = LayoutPadding(start = 0.dp, top = 16.dp, end = 0.dp, bottom = 0.dp),
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
        Box(
            modifier = LayoutSize(8.dp, 8.dp),
            backgroundColor = contentColor(),
            shape = CircleShape
        ) {
            // empty box
        }
        Text(
            modifier = LayoutFlexible(1f),
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
    val typography = MaterialTheme.typography()
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

private fun paragraphToAnnotatedString(paragraph: Paragraph, typography: Typography): AnnotatedString {
    val styles: List<AnnotatedString.Item<SpanStyle>> = paragraph.markups
        .map { it: Markup -> it.toAnnotatedStringItem(typography) }
    return AnnotatedString(text = paragraph.text, spanStyles = styles)
}

fun Markup.toAnnotatedStringItem(typography: Typography): AnnotatedString.Item<SpanStyle> {
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

@Preview
@Composable
fun preview() {
    PostContent(post = post3)
}
