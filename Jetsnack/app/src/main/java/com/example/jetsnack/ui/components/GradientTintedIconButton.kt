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

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.graphics.BlendMode
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.IconButton
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun JetsnackGradientTintedIconButton(
    asset: VectorAsset,
    onClick: () -> Unit,
    colors: List<Color> = JetsnackTheme.colors.interactiveSecondary
) {
    // This should use a layer + srcIn but needs investigation
    val blendMode = if (JetsnackTheme.colors.isDark) BlendMode.darken else BlendMode.plus
    IconButton(onClick = onClick) {
        Icon(
            asset = asset,
            modifier = Modifier.diagonalGradientTint(
                colors = colors,
                blendMode = blendMode
            )
        )
    }
}
