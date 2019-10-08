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
import androidx.compose.ambient
import androidx.compose.composer
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Draw
import androidx.ui.core.Px
import androidx.ui.core.dp
import androidx.ui.engine.geometry.Offset
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.foundation.shape.border.Border
import androidx.ui.foundation.shape.border.DrawBorder
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Image
import androidx.ui.graphics.Paint
import androidx.ui.graphics.imageFromResource
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.material.MaterialColors
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeColor
import com.android.tools.preview.Preview
import com.example.jetnews.R

@Composable
fun SelectTopicButton(
    onSelected: ((Boolean) -> Unit)? = null,
    selected: Boolean = false
) {
    Toggleable(checked = selected, onCheckedChange = onSelected) {
        Container(width = 36.dp, height = 36.dp) {
            if (selected) {
                DrawSelectTopicButtonOn()
            } else {
                DrawSelectTopicButtonOff()
            }
        }
    }
}

@Composable
private fun DrawImageCentered(image: Image, paint: Paint) {
    Draw { canvas, parentSize ->
        val imageOffset =
            Offset(
                (parentSize.width - Px(image.width.toFloat())).value / 2,
                (parentSize.height - Px(image.height.toFloat())).value / 2
            )
        canvas.drawImage(image, imageOffset, paint)
    }
}

@Composable
private fun DrawSelectTopicButtonOn() {
    val context = +ambient(ContextAmbient)
    val strokeWidth = 2.dp
    val image = +memo { imageFromResource(context.resources, R.drawable.ic_check_24px) }
    val paint = +memo {
        Paint().apply {
            color = +themeColor { primary }
            isAntiAlias = true
        }
    }
    Draw { canvas, parentSize ->
        val radius = parentSize.width.value / 2
        // toPx() is available only within Draw
        paint.strokeWidth = strokeWidth.toPx().value
        canvas.drawCircle(Offset(radius, radius), radius, paint)
    }
    DrawImageCentered(image, paint)
}

@Composable
private fun DrawSelectTopicButtonOff() {
    val paint = +memo { Paint() }

    val strokeWidth = 2.dp
    val context = +ambient(ContextAmbient)
    val color = (+themeColor { onSurface }).copy(alpha = 0.12f)
    val image = +memo { imageFromResource(context.resources, R.drawable.ic_add_24px) }
    DrawBorder(CircleShape, Border(color, strokeWidth))
    DrawImageCentered(image, paint)
}

@Preview("Off")
@Composable
fun SelectTopicButtonPreviewOff() {
    SelectTopicButtonPreviewTemplate(lightThemeColors, false)
}

@Preview("On")
@Composable
fun SelectTopicButtonPreviewOn() {
    SelectTopicButtonPreviewTemplate(lightThemeColors, true)
}

@Preview("Off - Dark")
@Composable
fun SelectTopicButtonPreviewOffDark() {
    SelectTopicButtonPreviewTemplate(darkThemeColors, false)
}

@Preview("On - Dark")
@Composable
fun SelectTopicButtonPreviewOnDark() {
    SelectTopicButtonPreviewTemplate(darkThemeColors, true)
}

@Composable
private fun SelectTopicButtonPreviewTemplate(themeColors: MaterialColors, selected: Boolean) {
    MaterialTheme(themeColors) {
        Surface {
            Padding(32.dp) {
                SelectTopicButton(selected = selected)
            }
        }
    }
}

