package com.example.jetcaster.ui.shared

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.HtmlCompat

/**
 * A container for text that should be HTML formatted. This container will handle building the
 * annotated string from [text], and enable text selection if [text] has any selectable element.
 */
@Composable
fun HtmlTextContainer(
    text: String,
    content: @Composable (AnnotatedString) -> Unit
) {
    val annotatedString = remember(key1 = text) {
        buildAnnotatedString {
            val htmlCompat = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
            append(htmlCompat)
        }
    }
    SelectionContainer {
        content(annotatedString)
    }
}
