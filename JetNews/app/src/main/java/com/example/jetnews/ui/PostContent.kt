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
import androidx.ui.core.Px
import androidx.ui.core.Text
import androidx.ui.core.WithDensity
import androidx.ui.core.dp
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.graphics.vector.compat.vectorResource
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.FlexRow
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeTextStyle
import androidx.ui.text.AnnotatedString
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.font.Font
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextDecoration
import androidx.ui.text.style.TextIndent
import com.example.jetnews.R

private const val defaultBodyLineHeight = 1.49f
private val defaultSpacerSize = 16.dp

private val codeFontFamily = FontFamily(font = Font(name = "fira_code_regular.ttf"))

private val italicTextStyle = (+themeTextStyle { body1 }).copy(fontStyle = FontStyle.Italic)
private val boldTextStyle = (+themeTextStyle { body1 }).copy(fontWeight = FontWeight.Bold)
private val codeTextStyle = (+themeTextStyle { body1 })
    .copy(background = Color.LightGray, fontFamily = codeFontFamily)
private val linkTextStyle = (+themeTextStyle { body1 }).copy(decoration = TextDecoration.Underline)

private val bulletParagraphStyle = ParagraphStyle(textIndent = TextIndent(firstLine = Px(30f)))
private val metadataTextStyle = +themeTextStyle { caption.copy(color = Color.Gray) }

@Composable
fun PostContent(post: Post) {
    VerticalScroller {
        Padding(left = defaultSpacerSize, right = defaultSpacerSize) {
            Column(crossAxisAlignment = CrossAxisAlignment.Start) {
                HeightSpacer(height = defaultSpacerSize)
                Text(text = post.title, style = +themeTextStyle { h4 })
                HeightSpacer(height = defaultSpacerSize)
                post.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = +themeTextStyle { subtitle1 },
                        paragraphStyle = ParagraphStyle(lineHeight = 1.49f)
                    )
                    HeightSpacer(height = defaultSpacerSize)
                }
                PostMetadata(metadata = post.metadata)
                HeightSpacer(height = defaultSpacerSize)
                PostContents(paragraphs = post.paragraphs)
            }
        }
    }
}

@Composable
fun PostMetadata(metadata: Metadata) {
    val postMetadata =
        "${metadata.author.name} on ${metadata.date} - ${metadata.readTimeMinutes} min read"
    Row {
        DefaultAuthorImage()
        WidthSpacer(width = 8.dp)
        Text(text = postMetadata, style = metadataTextStyle)
    }
}

@Composable
fun DefaultAuthorImage() {
    val defaultImage = +vectorResource(R.drawable.ic_account_circle_black)
    WithDensity {
        val width = defaultImage.defaultWidth.toDp()
        val height = defaultImage.defaultHeight.toDp()
        Container(width = width, height = height) {
            DrawVector(vectorImage = defaultImage)
        }
    }
}

@Composable
fun PostContents(paragraphs: List<Paragraph>) {
    paragraphs.forEach {
        Padding(top = 4.dp, bottom = 4.dp) {
            Paragraph(paragraph = it)
        }
    }
}

@Composable
fun Paragraph(paragraph: Paragraph) {
    val (textStyle, paragraphStyle) = paragraph.type.getTextAndParagraphStyle()

    val annotatedString = paragraphToAnnotatedString(paragraph)
    when (paragraph.type) {
        is ParagraphType.Bullet -> BulletParagraph(
            text = annotatedString,
            textStyle = textStyle,
            paragraphStyle = paragraphStyle
        )
        is ParagraphType.CodeBlock -> CodeBlockParagraph(
            text = annotatedString,
            textStyle = textStyle,
            paragraphStyle = paragraphStyle
        )
        else -> Text(text = annotatedString, style = textStyle, paragraphStyle = paragraphStyle)
    }
}

@Composable
fun CodeBlockParagraph(
    text: AnnotatedString,
    textStyle: TextStyle,
    paragraphStyle: ParagraphStyle
) {
    Surface(color = Color.LightGray) {
        Text(text = text, style = textStyle, paragraphStyle = paragraphStyle)
    }
}

@Composable
fun BulletParagraph(text: AnnotatedString, textStyle: TextStyle, paragraphStyle: ParagraphStyle) {
    FlexRow {
        inflexible {
            Container(width = 8.dp, height = 8.dp) {
                DrawShape(shape = CircleShape, color = Color.DarkGray)
            }
        }
        flexible(flex = 1f) {
            Text(text = text, style = textStyle, paragraphStyle = paragraphStyle)
        }
    }
}

private fun ParagraphType.getTextAndParagraphStyle(): Pair<TextStyle, ParagraphStyle> {
    var textStyle: TextStyle = +themeTextStyle { body1 }
    var paragraphStyle = ParagraphStyle()

    when (this) {
        is ParagraphType.Caption -> textStyle = +themeTextStyle { body1 }
        ParagraphType.Title -> textStyle = +themeTextStyle { h4 }
        ParagraphType.Subtitle -> textStyle = +themeTextStyle { subtitle1 }
        ParagraphType.Text -> {
            textStyle = +themeTextStyle { body1 }
            paragraphStyle = paragraphStyle.copy(lineHeight = defaultBodyLineHeight)
        }
        ParagraphType.CodeBlock -> textStyle = codeTextStyle
        ParagraphType.Quote -> textStyle = +themeTextStyle { body1 }
        ParagraphType.Bullet -> paragraphStyle = bulletParagraphStyle
    }
    return textStyle to paragraphStyle
}

private fun paragraphToAnnotatedString(paragraph: Paragraph): AnnotatedString {
    val styles = paragraph.markups.map { it.toAnnotatedStringItem() }
    return AnnotatedString(text = paragraph.text, textStyles = styles)
}

private fun Markup.toAnnotatedStringItem(): AnnotatedString.Item<TextStyle> {
    return when (this.type) {
        is MarkupType.Italic -> AnnotatedString.Item(italicTextStyle, start, end)
        is MarkupType.Link -> AnnotatedString.Item(linkTextStyle, start, end)
        is MarkupType.Bold -> AnnotatedString.Item(boldTextStyle, start, end)
        is MarkupType.Code -> AnnotatedString.Item(codeTextStyle, start, end)
    }
}
