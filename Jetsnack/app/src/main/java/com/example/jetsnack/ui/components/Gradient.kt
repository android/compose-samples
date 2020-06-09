/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.ui.components

import androidx.ui.core.Modifier
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.drawBorder
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.graphics.LinearGradient
import androidx.ui.graphics.Shape
import androidx.ui.graphics.TileMode
import androidx.ui.unit.dp

fun Modifier.gradientBackground(
    colors: List<Color>,
    width: Float,
    offset: Float
) = drawBackground(
    HorizontalGradient(
        colors,
        startX = -offset,
        endX = width - offset,
        tileMode = TileMode.Mirror
    )
)

fun Modifier.gradientBorder(
    colors: List<Color>,
    shape: Shape
) = drawBorder(
    size = 2.dp,
    shape = shape,
    brush = LinearGradient(
        colors = colors,
        startX = 0f,
        startY = 0f,
        endX = 111f,
        endY = 59f
    )
)
