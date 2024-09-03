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

package com.example.jetlagged.backgrounds

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import com.example.jetlagged.ui.theme.White
import com.example.jetlagged.ui.theme.Yellow
import com.example.jetlagged.ui.theme.YellowVariant

fun Modifier.simpleGradient(): Modifier =
    drawWithCache {
        val gradientBrush = Brush.verticalGradient(listOf(Yellow, YellowVariant, White))
        onDrawBehind {
            drawRect(gradientBrush, alpha = 1f)
        }
    }
