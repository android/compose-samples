/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat.conversation

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.sp

// Regex containing the syntax tokens
val symbolPattern by lazy {
    Regex("""(https?://[^\s\t\n]+)|(`[^`]+`)|(@\w+)|(\*[\w]+\*)|(_[\w]+_)|(~[\w]+~)""")
}

// Accepted annotations for the ClickableTextWrapper
enum class SymbolAnnotationType {
    PERSON,
    LINK,
}
typealias StringAnnotation = AnnotatedString.Range<String>
// Pair returning styled content and annotation for ClickableText when matching syntax token
typealias SymbolAnnotation = Pair<AnnotatedString, StringAnnotation?>

/**
 * Format a message following Markdown-lite syntax
 * | @username -> bold, primary color and clickable element
 * | http(s)://... -> clickable link, opening it into the browser
 * | *bold* -> bold
 * | _italic_ -> italic
 * | ~strikethrough~ -> strikethrough
 * | `MyClass.myMethod` -> inline code styling
 *
 * @param text contains message to be parsed
 * @param onPersonClick called with the username when an @mention is clicked
 * @return AnnotatedString with links for URLs and @mentions
 */
@Composable
fun messageFormatter(
    text: String,
    primary: Boolean,
    onPersonClick: (String) -> Unit = {},
    annotatedContent: AnnotatedString? = null,
): AnnotatedString {
    val colorScheme = MaterialTheme.colorScheme
    val currentOnPersonClick by rememberUpdatedState(onPersonClick)
    return remember(text, primary, colorScheme, annotatedContent) {
        val tokens = symbolPattern.findAll(text)
        // Rich-text spans index into the raw [text]. Markdown-lite tokens drop their delimiters
        // when rendered, so track where each raw boundary ends up in the built string. Only
        // allocated when there are spans to remap.
        val extraSpans = annotatedContent?.spanStyles.orEmpty()
        val rawToBuilt = if (extraSpans.isNotEmpty()) IntArray(text.length + 1) else null

        buildAnnotatedString {
            var cursorPosition = 0

            val codeSnippetBackground =
                if (primary) {
                    colorScheme.secondary
                } else {
                    colorScheme.surface
                }

            for (token in tokens) {
                val first = token.range.first
                if (rawToBuilt != null) {
                    val segmentStart = length
                    for (p in cursorPosition until first) {
                        rawToBuilt[p] = segmentStart + (p - cursorPosition)
                    }
                }
                append(text.substring(cursorPosition, first))

                val (annotatedString, stringAnnotation) = getSymbolAnnotation(
                    matchResult = token,
                    colorScheme = colorScheme,
                    primary = primary,
                    codeSnippetBackground = codeSnippetBackground,
                )

                if (rawToBuilt != null) {
                    val tokenStart = length
                    val content = annotatedString.text
                    // Number of leading delimiter characters stripped from the raw token.
                    val lead = if (content.isEmpty()) 0 else token.value.indexOf(content).coerceAtLeast(0)
                    for (p in token.range) {
                        rawToBuilt[p] = tokenStart + (p - first - lead).coerceIn(0, content.length)
                    }
                }

                val link = stringAnnotation?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> LinkAnnotation.Url(annotation.item)

                        SymbolAnnotationType.PERSON.name -> LinkAnnotation.Clickable(annotation.item) {
                            currentOnPersonClick(annotation.item)
                        }

                        else -> null
                    }
                }
                if (link != null) {
                    withLink(link) { append(annotatedString) }
                } else {
                    append(annotatedString)
                }

                cursorPosition = token.range.last + 1
            }

            // Trailing plain text (or the whole text when there were no tokens).
            if (rawToBuilt != null) {
                val segmentStart = length
                for (p in cursorPosition..text.length) {
                    rawToBuilt[p] = segmentStart + (p - cursorPosition)
                }
            }
            append(text.substring(cursorPosition))

            if (rawToBuilt != null) {
                applyRichTextSpans(extraSpans, rawToBuilt)
            }
        }
    }
}

// SpanStyle merging replaces (rather than combines) textDecoration, so overlapping underline and
// strikethrough ranges need an explicit combined style.
private val UnderlineLineThroughSpanStyle = SpanStyle(
    textDecoration = TextDecoration.combine(
        listOf(TextDecoration.Underline, TextDecoration.LineThrough),
    ),
)

/**
 * Applies rich-text [spans] (indexed into the raw message text) to this builder, remapping each
 * boundary through [rawToBuilt].
 */
private fun AnnotatedString.Builder.applyRichTextSpans(spans: List<AnnotatedString.Range<SpanStyle>>, rawToBuilt: IntArray) {
    val maxRaw = rawToBuilt.lastIndex
    var underlineRanges: MutableList<IntRange>? = null
    var strikethroughRanges: MutableList<IntRange>? = null

    for (span in spans) {
        val start = rawToBuilt[span.start.coerceIn(0, maxRaw)]
        val end = rawToBuilt[span.end.coerceIn(0, maxRaw)].coerceIn(start, length)
        if (start >= end) continue
        addStyle(span.item, start, end)
        when (span.item.textDecoration) {
            TextDecoration.Underline ->
                (underlineRanges ?: mutableListOf<IntRange>().also { underlineRanges = it })
                    .add(start until end)

            TextDecoration.LineThrough ->
                (strikethroughRanges ?: mutableListOf<IntRange>().also { strikethroughRanges = it })
                    .add(start until end)
        }
    }

    val underlines = underlineRanges ?: return
    val strikethroughs = strikethroughRanges ?: return
    for (u in underlines) {
        for (s in strikethroughs) {
            val overlapStart = maxOf(u.first, s.first)
            val overlapEnd = minOf(u.last, s.last) + 1
            if (overlapStart < overlapEnd) {
                addStyle(UnderlineLineThroughSpanStyle, overlapStart, overlapEnd)
            }
        }
    }
}

/**
 * Map regex matches found in a message with supported syntax symbols
 *
 * @param matchResult is a regex result matching our syntax symbols
 * @return pair of AnnotatedString with annotation (optional) used inside the ClickableText wrapper
 */
private fun getSymbolAnnotation(
    matchResult: MatchResult,
    colorScheme: ColorScheme,
    primary: Boolean,
    codeSnippetBackground: Color,
): SymbolAnnotation {
    return when (matchResult.value.first()) {
        '@' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
                    color = if (primary) colorScheme.inversePrimary else colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                ),
            ),
            StringAnnotation(
                item = matchResult.value.substring(1),
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.PERSON.name,
            ),
        )

        '*' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('*'),
                spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
            ),
            null,
        )

        '_' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('_'),
                spanStyle = SpanStyle(fontStyle = FontStyle.Italic),
            ),
            null,
        )

        '~' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('~'),
                spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough),
            ),
            null,
        )

        '`' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('`'),
                spanStyle = SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    background = codeSnippetBackground,
                    baselineShift = BaselineShift(0.2f),
                ),
            ),
            null,
        )

        'h' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
                    color = if (primary) colorScheme.inversePrimary else colorScheme.primary,
                ),
            ),
            StringAnnotation(
                item = matchResult.value,
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.LINK.name,
            ),
        )

        else -> SymbolAnnotation(AnnotatedString(matchResult.value), null)
    }
}
