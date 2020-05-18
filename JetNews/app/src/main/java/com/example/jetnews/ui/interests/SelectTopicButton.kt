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

package com.example.jetnews.ui.interests

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSize
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.ui.ThemedPreview

@Composable
fun SelectTopicButton(
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    if (selected) {
        SelectTopicButtonOn(modifier.preferredSize(36.dp, 36.dp))
    } else {
        SelectTopicButtonOff(modifier.preferredSize(36.dp, 36.dp))
    }
}

@Composable
private fun SelectTopicButtonOn(modifier: Modifier = Modifier) {
    Box(
        backgroundColor = MaterialTheme.colors.primary,
        shape = CircleShape,
        modifier = modifier
    ) {
        Icon(
            asset = vectorResource(R.drawable.ic_check),
            tint = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
private fun SelectTopicButtonOff(modifier: Modifier = Modifier) {
    val borderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
    Box(
        backgroundColor = borderColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        Icon(vectorResource(R.drawable.ic_add))
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
