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

package com.example.jetsnack.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentColorAmbient
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.preferredSizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonConstants
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun JetsnackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonShape,
    border: BorderStroke? = null,
    backgroundGradient: List<Color> = JetsnackTheme.colors.interactivePrimary,
    disabledBackgroundGradient: List<Color> = JetsnackTheme.colors.interactiveSecondary,
    contentColor: Color = JetsnackTheme.colors.textInteractive,
    disabledContentColor: Color = JetsnackTheme.colors.textHelp,
    padding: PaddingValues = ButtonConstants.DefaultContentPadding,
    text: @Composable () -> Unit
) {
    Providers(ContentColorAmbient provides if (enabled) contentColor else disabledContentColor) {
        ProvideTextStyle(MaterialTheme.typography.button) {
            Box(
                modifier = modifier
                    // Todo move to ButtonConstants.DefaultMinWidth / DefaultMinHeight
                    .preferredSizeIn(minWidth = 64.dp, minHeight = 36.dp)
                    .clip(shape)
                    .horizontalGradientBackground(
                        colors = if (enabled) backgroundGradient else disabledBackgroundGradient
                    )
                    .then(if (border != null) Modifier.border(border, shape) else Modifier)
                    .semantics(mergeAllDescendants = true, properties = { })
                    .clickable(onClick = onClick, enabled = enabled),
                paddingStart = padding.start,
                paddingTop = padding.top,
                paddingEnd = padding.end,
                paddingBottom = padding.bottom,
                gravity = ContentGravity.Center,
                children = text
            )
        }
    }
}

private val ButtonShape = RoundedCornerShape(percent = 50)
