/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.designsystem.component

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.HtmlCompat

/**
 * A container for text that should be HTML formatted. This container will handle building the
 * annotated string from [text], and enable text selection if [text] has any selectable element.
 *
 * TODO: Remove/update once the project is using Compose 1.7 as that version provides improved
 * support for HTML formatting.
 * See: https://developer.android.com/jetpack/androidx/releases/compose-foundation#1.7.0-alpha07
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
