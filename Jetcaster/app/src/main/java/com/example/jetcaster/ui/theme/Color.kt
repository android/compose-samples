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

package com.example.jetcaster.ui.theme

import androidx.compose.Composable
import androidx.ui.graphics.Color
import androidx.ui.graphics.compositeOver
import androidx.ui.material.ColorPalette
import androidx.ui.material.darkColorPalette

/**
 * Return the fully opaque color that results from compositing [onSurface] atop [surface] with the
 * given [alpha]. Useful for situations where semi-transparent colors are undesirable.
 */
@Composable
fun ColorPalette.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(surface)
}

val Orange300 = Color(0xFFFFBC51)
val Orange500 = Color(0xFFFF9F0A)

val Colors = darkColorPalette(
    primary = Orange300,
    primaryVariant = Orange500,
    secondary = Orange300
)
