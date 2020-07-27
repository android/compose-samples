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
import androidx.compose.Providers
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.core.semantics.semantics
import androidx.ui.foundation.Border
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentColorAmbient
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBorder
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.layout.InnerPadding
import androidx.ui.layout.preferredSizeIn
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun JetsnackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonShape,
    border: Border? = null,
    backgroundGradient: List<Color> = JetsnackTheme.colors.interactivePrimary,
    disabledBackgroundGradient: List<Color> = JetsnackTheme.colors.interactiveSecondary,
    contentColor: Color = JetsnackTheme.colors.textInteractive,
    disabledContentColor: Color = JetsnackTheme.colors.textHelp,
    padding: InnerPadding = Button.DefaultInnerPadding,
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
                    + (if (border != null) Modifier.drawBorder(border, shape) else Modifier)
                    .semantics(mergeAllDescendants = true)
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
