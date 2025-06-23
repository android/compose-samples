/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.jetsnack.widget.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.width
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics

/**
 * Component to build an item within a list. Lists are a continuous, vertical indexes of text or
 * images.
 *
 * Apply padding to the item using the [androidx.glance.layout.padding] modifier.
 *
 * @param headlineContent the [Composable] headline content of the list item; typically a 1-line
 *                        prominent text within the list item
 * @param modifier [GlanceModifier] to be applied to the list item
 * @param contentSpacing spacing between the leading, center and trailing sections; default 16.dp
 * @param supportingContent 1-2 line text to be displayed below the headline text of the list item
 * @param leadingContent the leading content of the list item such as an image, icon, or a selection
 *                       control such as checkbox, switch, or a radio button
 * @param trailingContent the trailing meta text, icon, or a selection control such as switch,
 *                        checkbox, or a radio button
 * @param onClick an option action to be performed on click of the list item.
 * @param itemContentDescription an optional text used by accessibility services to describe what
 *                               this list item represents. If not provided, the non-clickable
 *                               content within the list item will be read out.
 */
@Composable
fun ListItem(
    headlineContent: @Composable (() -> Unit),
    modifier: GlanceModifier = GlanceModifier,
    contentSpacing: Dp = 16.dp,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: Action? = null,
    itemContentDescription: String? = null,
) {
    val listItemModifier = if (itemContentDescription != null) {
        modifier.semantics { contentDescription = itemContentDescription }
    } else {
        modifier
    }

    Row(
        modifier = listItemModifier.maybeClickable(onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Leading
        leadingContent?.let {
            it()
            Spacer(modifier = GlanceModifier.width(contentSpacing))
        }
        // Center
        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            headlineContent()
            supportingContent?.let { it() }
        }
        // Trailing
        trailingContent?.let {
            Spacer(modifier = GlanceModifier.width(contentSpacing))
            it()
        }
    }
}

private fun GlanceModifier.maybeClickable(action: Action?): GlanceModifier = if (action != null) {
    this.clickable(action)
} else {
    this
}
