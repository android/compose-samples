/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetnews.glance.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.unit.ColorProvider
import com.example.jetnews.glance.ui.theme.JetnewsGlanceColorScheme

/**
 * A thin line that groups content in lists and layouts.
 *
 * @param thickness thickness in dp of this divider line.
 * @param color color of this divider line.
 */
@Composable
fun Divider(
    thickness: Dp = DividerDefaults.Thickness,
    color: ColorProvider = DividerDefaults.color
) {
    Spacer(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

/** Default values for [Divider] */
object DividerDefaults {
    /** Default thickness of a divider. */
    val Thickness: Dp = 1.dp

    /** Default color of a divider. */
    val color: ColorProvider @Composable get() = JetnewsGlanceColorScheme.outlineVariant
}
