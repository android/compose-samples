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
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.ui.darkThemeColors
import com.example.jetnews.ui.lightThemeColors

@Composable
fun SelectTopicButton(
    modifier: Modifier = Modifier.None,
    selected: Boolean = false
) {
    if (selected) {
        SelectTopicButtonOn(modifier + LayoutSize(36.dp, 36.dp))
    } else {
        SelectTopicButtonOff(modifier + LayoutSize(36.dp, 36.dp))
    }
}

@Composable
private fun SelectTopicButtonOn(modifier: Modifier = Modifier.None) {
    Box(
        backgroundColor = MaterialTheme.colors().primary,
        shape = CircleShape,
        modifier = modifier
    ) {
        DrawVector(vectorResource(R.drawable.ic_check))
    }
}

@Composable
private fun SelectTopicButtonOff(modifier: Modifier = Modifier.None) {
    val borderColor = MaterialTheme.colors().onSurface.copy(alpha = 0.12f)
    Box(
        backgroundColor = borderColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        DrawVector(
            vectorImage = vectorResource(R.drawable.ic_add),
            tintColor = contentColor()
        )
    }
}

@Preview("Off")
@Composable
fun SelectTopicButtonPreviewOff() {
    SelectTopicButtonPreviewTemplate(
        lightThemeColors,
        false
    )
}

@Preview("On")
@Composable
fun SelectTopicButtonPreviewOn() {
    SelectTopicButtonPreviewTemplate(
        lightThemeColors,
        true
    )
}

@Preview("Off - Dark")
@Composable
fun SelectTopicButtonPreviewOffDark() {
    SelectTopicButtonPreviewTemplate(
        darkThemeColors,
        false
    )
}

@Preview("On - Dark")
@Composable
fun SelectTopicButtonPreviewOnDark() {
    SelectTopicButtonPreviewTemplate(
        darkThemeColors,
        true
    )
}

@Composable
private fun SelectTopicButtonPreviewTemplate(themeColors: ColorPalette, selected: Boolean) {
    MaterialTheme(themeColors) {
        Surface {
            SelectTopicButton(
                modifier = LayoutPadding(32.dp),
                selected = selected
            )
        }
    }
}
