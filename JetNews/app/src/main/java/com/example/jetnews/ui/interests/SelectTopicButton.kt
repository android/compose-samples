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

package com.example.jetnews.ui.interests

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.ui.ThemedPreview

@Composable
fun SelectTopicButton(
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val icon = if (selected) Icons.Filled.Done else Icons.Filled.Add
    val iconColor = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary
    val borderColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
    val backgroundColor = if (selected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onPrimary
    }
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.size(36.dp, 36.dp)
    ) {
        Image(
            imageVector = icon,
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier.padding(8.dp),
            contentDescription = null // toggleable at higher level
        )
    }
}

@Preview("Off")
@Composable
fun SelectTopicButtonPreviewOff() {
    SelectTopicButtonPreviewTemplate(
        darkTheme = false,
        selected = false
    )
}

@Preview("On")
@Composable
fun SelectTopicButtonPreviewOn() {
    SelectTopicButtonPreviewTemplate(
        darkTheme = false,
        selected = true
    )
}

@Preview("Off - dark theme")
@Composable
fun SelectTopicButtonPreviewOffDark() {
    SelectTopicButtonPreviewTemplate(
        darkTheme = true,
        selected = false
    )
}

@Preview("On - dark theme")
@Composable
fun SelectTopicButtonPreviewOnDark() {
    SelectTopicButtonPreviewTemplate(
        darkTheme = true,
        selected = true
    )
}

@Composable
private fun SelectTopicButtonPreviewTemplate(
    darkTheme: Boolean = false,
    selected: Boolean
) {
    ThemedPreview(darkTheme) {
        SelectTopicButton(
            modifier = Modifier.padding(32.dp),
            selected = selected
        )
    }
}
