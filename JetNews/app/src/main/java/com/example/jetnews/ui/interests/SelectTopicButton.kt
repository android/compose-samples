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
import androidx.compose.unaryPlus
import androidx.ui.core.Modifier
import androidx.ui.core.dp
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.border.Border
import androidx.ui.foundation.shape.border.DrawBorder
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.Container
import androidx.ui.layout.Size
import androidx.ui.layout.Spacing
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.ui.darkThemeColors
import com.example.jetnews.ui.lightThemeColors

@Composable
fun SelectTopicButton(
    modifier: Modifier = Modifier.None,
    onSelected: ((Boolean) -> Unit)? = null,
    selected: Boolean = false
) {
    Ripple(bounded = false) {
        Toggleable(selected, onSelected) {
            Container(modifier = modifier wraps Size(36.dp, 36.dp)) {
                if (selected) {
                    DrawSelectTopicButtonOn()
                } else {
                    DrawSelectTopicButtonOff()
                }
            }
        }
    }
}

@Composable
private fun DrawSelectTopicButtonOn() {
    DrawShape(
        shape = CircleShape,
        color = (+MaterialTheme.colors()).primary
    )
    DrawVector(+vectorResource(R.drawable.ic_check))
}

@Composable
private fun DrawSelectTopicButtonOff() {
    val borderColor = ((+MaterialTheme.colors()).onSurface).copy(alpha = 0.12f)
    DrawBorder(
        shape = CircleShape,
        border = Border(borderColor, 2.dp)
    )
    DrawVector(
        vectorImage = +vectorResource(R.drawable.ic_add),
        tintColor = (+MaterialTheme.colors()).primary
    )
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
                modifier = Spacing(32.dp),
                selected = selected
            )
        }
    }
}
